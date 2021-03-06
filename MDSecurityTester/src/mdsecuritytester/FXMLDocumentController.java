/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mdsecuritytester;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;


/**
 *
 * @author anirudh duggal
 */
public class FXMLDocumentController implements Initializable {
    
    //button and text input declaration on HL7 Scanner Tab
    
     @FXML
    private TextField hl7scanner_ip_address_textField;

    @FXML
    private TextField hl7scanner_port_textField;

    @FXML
    private TextField hl7scanner_timeout_textField;

    @FXML
    private Button hl7scanner_scan_btn;

    @FXML
    private TextArea hl7scanner_console_textField;

    @FXML
    private Button hl7scanner_clear_btn;

    @FXML
    private TextArea hl7messenger_console_textField;

    @FXML
    private TextField hl7messenger_ip_address_textField;

    @FXML
    private TextField hl7messenger_port_textField;

    @FXML
    private TextField hl7messenger_repeat_textField;

    @FXML
    private Button hl7messenger_messageSend_btn;

    @FXML
    private TextArea hl7messenger_response_console_textField;

    @FXML
    private Button hl7messenger_clear_response_console_btn;

    @FXML
    private TextField hl7server_port_textField;

    @FXML
    private Button hl7server_btn_stop;

    @FXML
    private TextArea hl7server_message_textField;

    @FXML
    private TextArea hl7server_console_textField;
    
    @FXML
    private TextArea analyzer_consoleOutput_textField;
    
    @FXML
    private ComboBox <String> hl7Generator_messageSelector_comboBox;
    
    @FXML
    private ComboBox <String> hl7Generator_messageVersion_comboBox;

    //Python variables
    String HL7ScannerPythonScriptName = "HL7Scanner.py";
    String HL7MessengerPythonScriptName = "HL7Messenger.py";
    String HL7ServerPythonScriptName = "HL7Server.py";
    String Helper_Eval_Script = "Eval.py";
    String HL7PcapAnalyzer = "HL7PcapAnalyzer.py";
    //universal runtime declaration
    Runtime runtimeProcess = Runtime.getRuntime();
    
    @FXML
    void onClickStartScanningAction(ActionEvent event) throws IOException  {
        
        String ip_address = hl7scanner_ip_address_textField.getText();
        String timeout = hl7scanner_timeout_textField.getText();
        String port = hl7scanner_port_textField.getText();
        
        //command to run HL7 Scanner
        String hl7Scanner_command="python "+HL7ScannerPythonScriptName+" -ip "+ip_address+" -p "+port+" -t "+timeout;
        
        System.out.println("Running command: "+hl7Scanner_command);
        
        Process p = runtimeProcess.exec(hl7Scanner_command);
        try
        {
   
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ret1 = (in.readLine());

            while(in.ready())
            {
                String ret = (in.readLine());
                hl7scanner_console_textField.appendText(ret);
                hl7scanner_console_textField.appendText("\n");
            }
            
        }
        catch(Exception e)
        {
            System.out.println(e);
            p.destroy();
        }
    }
    
    
    @FXML
    void onClickClearConsoleAction(ActionEvent event) {
        
        hl7scanner_console_textField.setText("");

    }
    
    @FXML
    void onClickSendMessageAction(ActionEvent event) throws IOException {
        
        String ip_address = hl7messenger_ip_address_textField.getText();
        String repeat = hl7messenger_repeat_textField.getText();
        String port = hl7messenger_port_textField.getText();
        String message = hl7messenger_console_textField.getText();
        String payloadValue ="";
        
        Pattern pythonPayload = Pattern.compile("\\$\\((.*?)\\)");
        Matcher pythonPayloadMatcher = pythonPayload.matcher(message);
        
        
        if(pythonPayloadMatcher.find())
        {
            System.out.println("Pattern Found");
            System.out.println("Pattern is "+pythonPayloadMatcher.group(1));
            String evalCommand = "python "+Helper_Eval_Script+" -e ";
            String encodePaynloadEvalProcess = Base64.getEncoder().encodeToString(pythonPayloadMatcher.group(1).getBytes("utf-8"));

            evalCommand = evalCommand +" "+encodePaynloadEvalProcess;
            
            Process payloadEvalProcess = runtimeProcess.exec(evalCommand);
            
            BufferedReader payloadEvalProcessOutput = new BufferedReader(new InputStreamReader(payloadEvalProcess.getInputStream()));
            payloadValue = (payloadEvalProcessOutput.readLine());
            System.out.println("Payload is: "+payloadValue);
            
            System.out.println("Final Output is: "+payloadValue);
                    
        }
        
        message = message.replaceAll("\\$\\((.*?)\\)", payloadValue);
        
        String hl7messenger_command="python "+HL7MessengerPythonScriptName+" -ip "+ip_address+" -p "+port+" -m "+message;
        
        System.out.println("Running command: "+hl7messenger_command);
        Process p = runtimeProcess.exec(hl7messenger_command);
        
        try
        {
            //ProcessBuilder pb = new ProcessBuilder("python",HL7MessengerPythonScriptName,ip_address,port,message);
            //Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ret1 = (in.readLine());

            while(in.ready())
            {
                String ret = (in.readLine());
                hl7messenger_response_console_textField.appendText(ret);
                hl7messenger_response_console_textField.appendText("\n");
            }
            
        }
        catch(Exception e)
        {
            System.out.println(e);
            p.destroy();

        }

    }
    
    @FXML
    void hl7messenger_onClickClearConsoleAction(ActionEvent event) {
        
        hl7messenger_response_console_textField.setText("");

    }
    
    @FXML
    void hl7server_onClickStartServer(ActionEvent event) throws IOException {
        
        String port = hl7server_port_textField.getText();
        String message = hl7server_message_textField.getText();
        
        String payloadValue ="";
        
        Pattern pythonPayload = Pattern.compile("\\$\\((.*?)\\)");
        Matcher pythonPayloadMatcher = pythonPayload.matcher(message);
        
        
        if(pythonPayloadMatcher.find())
        {
            System.out.println("Pattern Found");
            System.out.println("Pattern is "+pythonPayloadMatcher.group(1));
            String evalCommand = "python "+Helper_Eval_Script+" -e ";
            String encodePaynloadEvalProcess = Base64.getEncoder().encodeToString(pythonPayloadMatcher.group(1).getBytes("utf-8"));

            evalCommand = evalCommand +" "+encodePaynloadEvalProcess;
            
            Process payloadEvalProcess = runtimeProcess.exec(evalCommand);
            
            BufferedReader payloadEvalProcessOutput = new BufferedReader(new InputStreamReader(payloadEvalProcess.getInputStream()));
            payloadValue = (payloadEvalProcessOutput.readLine());
            System.out.println("Payload is: "+payloadValue);
            
            System.out.println("Final Output is: "+payloadValue);
                    
        }
        
        message = message.replaceAll("\\$\\((.*?)\\)", payloadValue);
        
        String hl7startServer_command= "python "+HL7ServerPythonScriptName+" "+ port +" "+message;
        
        System.out.println("Running command: "+hl7startServer_command);
        Process p = runtimeProcess.exec(hl7startServer_command);
        
        try
        {
            
            //ProcessBuilder pb = new ProcessBuilder("python",HL7MessengerPythonScriptName,ip_address,port,message);
            //Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String ret1 = (in.readLine());

            while(in.ready())
            {
                String ret = (in.readLine());
                hl7server_console_textField.appendText(ret);
                hl7server_console_textField.appendText("\n");
            }
            
        }
        catch(Exception e)
        {
            System.out.println(e);
            p.destroy();

        }

    }

    
    @FXML
    void hl7server_onClickStopServer(ActionEvent event) throws IOException {
        
        String killCommand = "kill $(ps aux | grep python"+  HL7ServerPythonScriptName + "| awk '{ print $2 }')";
        
        System.out.println("Services killed");
        
        Process p = runtimeProcess.exec(killCommand);
        
        try
        {
    
            p.destroy();
            
        }
        catch(Exception e)
        {
            System.out.println(e);
            p.destroy();

        }

    }
    
      @FXML
    void analyzer_selectPcapFileAction(ActionEvent event) throws IOException {
        
            FileChooser selectPcapFile = new FileChooser();
            selectPcapFile.setTitle("Select PCAP file");
            File selectedPcapFile = selectPcapFile.showOpenDialog(null);
            
            if(selectedPcapFile != null)
            {
                System.out.println("Selected file is "+selectedPcapFile.getAbsoluteFile());
                
                 String hl7startPcapAnalysis_command= "python "+HL7PcapAnalyzer+" -f "+selectedPcapFile;
        
                System.out.println("Running command: "+hl7startPcapAnalysis_command);
                Process p = runtimeProcess.exec(hl7startPcapAnalysis_command);

                try
                {

                    //ProcessBuilder pb = new ProcessBuilder("python",HL7MessengerPythonScriptName,ip_address,port,message);
                    //Process p = pb.start();

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String ret1 = (in.readLine());

                    while(in.ready())
                    {
                        String ret = (in.readLine());
                        analyzer_consoleOutput_textField.appendText(ret);
                        analyzer_consoleOutput_textField.appendText("\n");
                    }

                }        
                catch(Exception e)
                {
                    System.out.println(e);
                    

                }
                
            }
            else
            {
                System.out.println("No File Selected");
            }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //initialize hl7Message Generator Tab
        ObservableList<String> messageSelectorList = FXCollections.observableArrayList("ADT", "BAR","DFT","MDM","MFN","ORM","ORU","QRY","RAS","RDE","RGV","SIU");
    
        hl7Generator_messageSelector_comboBox.setItems(messageSelectorList);
        hl7Generator_messageSelector_comboBox.setValue("ADT");
        
        
        ObservableList <String> messageVersionSelectorList = FXCollections.observableArrayList("v2.1", "v2.2","v2.3","v2.3.1","v2.4","v2.5","v2.5.1","v2.6","v2.7","v2.8","v2.8.1");
    
        hl7Generator_messageVersion_comboBox.setItems(messageVersionSelectorList);
        hl7Generator_messageVersion_comboBox.setValue("v2.7");
    }    
    
    
}
