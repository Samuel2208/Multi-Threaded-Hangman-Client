import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import jdk.internal.foreign.PlatformLayouts;


public class Controller extends Thread implements Initializable{
    Socket socketClient;

    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<Serializable> callback;
    @FXML
    private Button cat1,cat2,cat3;

    @FXML
    private Text wordDisplay,remainingAttempts,characterDraw;

    @FXML
    private TextField portIn;

    private static int attemptColor =0,attemptStates =0,attemptPets =0, lengthOfWord =0;

    private AtomicInteger numGuess = new AtomicInteger(0);
    ArrayList<Character> secretWord = new ArrayList<>();

    private static boolean isDisabled1 = false, isDisabled2 = false, isDisabled3 =false;


    private Stage gameOverPopupStage;



    //static so each instance of controller can access to update
    private static String textEntered = "";

    public void initialize(URL url, ResourceBundle rb){

    }


    public void connectMthd(ActionEvent e) throws IOException{
        textEntered = portIn.getText();
        portIn.clear();
        portIn.setPromptText("Pressed");
        try {
            int portConv = Integer.parseInt(textEntered);

            if (connectToPort(portConv)){
                switchToPlay(e);
            }else{
                portIn.setPromptText("Invalid Port");
            }
        }
        catch(NumberFormatException  f) {
            f.printStackTrace();
        }

    }

//    public void connectMthdEnter(javafx.scene.input.KeyEvent e) throws IOException{
//        if(e.getCode().getName().equals("Enter")){
//            textEntered = portIn.getText();
//            portIn.clear();
//            portIn.setPromptText("Pressed");
//            try {
//                int portConv = Integer.parseInt(textEntered);
//
//                if (connectToPort(portConv)){
//                    ActionEvent holdEvent = new ActionEvent();
//                    switchToPlay(holdEvent);
//                }else{
//                    portIn.setPromptText("Invalid Port");
//                }
//            }
//            catch(NumberFormatException  f) {
//                f.printStackTrace();
//            }
//        }
//    }

    private boolean connectToPort(int port){
        try {
            // Successful connection
            socketClient = new Socket("localhost", port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
            return true;
        } catch (IOException e) {
            // Connection failed
            return false;
        }
    }


    private void switchToPlay(ActionEvent e){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FXMLPlay.fxml"));

            Parent root = loader.load();

            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            Scene anotherScene = new Scene(root, 500, 500);

            Stage currentStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/optionPlay.css");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void resetGame(Stage previousStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FXMLPlay.fxml"));

            Parent root = loader.load();

            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            Scene anotherScene = new Scene(root, 500, 500);



            Stage currentStage = previousStage;
//            Stage currentStage = new Stage();
            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/optionPlay.css");

            playController.isDisabled1 = false;
            playController.isDisabled2 = false;
            playController.isDisabled3 = false;
            playController.attemptColor = 0;
            playController.attemptPets = 0;
            playController.attemptStates = 0;


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void switchToPlayNew(Stage previousStage,Integer BtnToDisable){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FXMLPlay.fxml"));

            Parent root = loader.load();

            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            Scene anotherScene = new Scene(root, 500, 500);



            Stage currentStage = previousStage;
//            Stage currentStage = new Stage();
            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/optionPlay.css");

            System.out.println(isDisabled1);
            System.out.println(isDisabled2);
            System.out.println(isDisabled3);
            System.out.println(BtnToDisable);
            if(BtnToDisable == 1 || playController.isDisabled1){
                playController.cat1.setDisable(true);
                playController.isDisabled1 = true;
            }
            if (BtnToDisable == 2 || playController.isDisabled2) {
                playController.cat2.setDisable(true);
                playController.isDisabled2 = true;
            }
            if (BtnToDisable == 3 || playController.isDisabled3) {
                playController.cat3.setDisable(true);
                playController.isDisabled3 = true;
            }


            if (isDisabled1 && isDisabled2 && isDisabled3){
                gameOverPop();
                Platform.runLater(()->resetGame(currentStage));
            }
            System.out.println(isDisabled1);
            System.out.println(isDisabled2);
            System.out.println(isDisabled3);

            playController.secretWord.clear();
            playController.lengthOfWord =0;


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    public void setOutputStream(ObjectOutputStream out) {
        this.out = out;
    }

    public void setInputStream(ObjectInputStream in) {
        this.in = in;
    }

    public int getAttemptColor(){
        return attemptColor;
    }

    public void setWordText(Text wordDisplay) {
        this.wordDisplay = wordDisplay;
    }

    public void instructionsPop(ActionEvent y){
        try {
            // Load the FXML file for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/instructions.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Instructions");

            Scene popScene = new Scene(root);
            popScene.getStylesheets().add("/Style/instructionsStyle.css");
            popupStage.setScene(popScene);

            // Show the popup
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wrongGuessPop(Integer remainingSection){
        Platform.runLater(() -> {
            try {
                // Load the FXML file for the popup
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wrongGuessPop.fxml"));
                Parent root = loader.load();
                Controller cont = loader.getController();

                // Create a new stage for the popup
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Guess Limit Reached");

                Scene popScene = new Scene(root);
                popScene.getStylesheets().add("/Style/popUpStyle.css");
                popupStage.setScene(popScene);

                String remainingStringify = remainingSection.toString();

                cont.remainingAttempts.setText(remainingStringify);
//                remainingAttempts.setText(remainingStringify);
                // Show the popup
                popupStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void gameOverPop(){
        Platform.runLater(() -> {
        try {
            // Load the FXML file for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameOver.fxml"));
            Parent root = loader.load();
            Controller cont = loader.getController();
            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Game Over");

            Scene popScene = new Scene(root);
            popScene.getStylesheets().add("/Style/popUpStyle.css");
            popupStage.setScene(popScene);

            cont.gameOverPopupStage = popupStage;

            // Show the popup
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        });
    }



    public void switchToPlay1(ActionEvent e) throws IOException{

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/hangman.fxml"));
            Parent root = loader.load();
            Scene anotherScene = new Scene(root, 500, 500);
            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            send("1");
            Stage currentStage = (Stage) cat1.getScene().getWindow();

            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/hangmanStyle.css");

            Platform.runLater(()-> {
                        playController.attemptColor = playController.attemptColor + 1;
                        System.out.println(playController.attemptColor);
                    });

            new Thread(() -> {
                while (true) {
                    try {
                        Object receivedObject = in.readObject();

                        if (receivedObject instanceof ArrayList) {
                            ArrayList<Character> receivedList = (ArrayList<Character>) receivedObject;

                            for (int i=0; i<lengthOfWord ; i++) {

                                if((receivedList.get(i) != secretWord.get(i)) && secretWord.get(i) == '_'
                                        && receivedList.get(i) != '#' ){
                                    secretWord.set(i,receivedList.get(i));
                                    System.out.println(receivedList.get(i));
                                }

                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

//
                                playController.wordDisplay.setText(stringBuilder.toString());

                            if(!stringBuilder.toString().contains("_")){
                                Platform.runLater(()->{
                                cat1.setDisable(true);
                                Platform.runLater(()->switchToPlayNew(currentStage,1));
                                });
                                break;
                            }
//


                        } else if (receivedObject instanceof Integer){
                            lengthOfWord = (Integer)receivedObject;

                            for (int i=0; i<lengthOfWord ; i++){
                                secretWord.add('_');
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

                            playController.wordDisplay.setText(stringBuilder.toString());


                        } else  {

                            numGuess.incrementAndGet();
                            StringBuilder drawCharacter = new StringBuilder();
                            if (numGuess.get()==1){
                                drawCharacter.append("\\");

                            } else if (numGuess.get()==2) {
                                drawCharacter.append("\\0");
                            } else if (numGuess.get()==3) {
                                drawCharacter.append("\\0/");
                            } else if (numGuess.get()==4) {
                                drawCharacter.append("\\0/\n  |");
                            } else if (numGuess.get()==5) {
                                drawCharacter.append("\\0/\n  |\n/");
                            }

                            if(numGuess.get() == 6){
                                if (playController.attemptColor == 3){
                                    gameOverPop();
                                    Platform.runLater(()->resetGame(currentStage));
                                    break;
                                }
                                drawCharacter.append("\\0/\n  |\n/ \\");
                                wrongGuessPop(3- playController.attemptColor);
                                Platform.runLater(()->switchToPlayNew(currentStage,-1));
                                break;
                            }
                            playController.characterDraw.setText(drawCharacter.toString());

                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }



                    if(wordDisplay!= null){
                        System.out.println("here");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Character line : secretWord) {
                            stringBuilder.append(line);
                        }

                        playController.wordDisplay.setText(stringBuilder.toString());
//                        System.out.println(wordDisplay);
                    }

                }
            }).start();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }


    public void switchToPlay2(ActionEvent e) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/hangman.fxml"));
            Parent root = loader.load();
            Scene anotherScene = new Scene(root, 500, 500);
            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            send("2");
            Stage currentStage = (Stage) cat2.getScene().getWindow();

            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/hangmanStyle.css");

            Platform.runLater(()-> {
                playController.attemptPets = playController.attemptPets +1;
                System.out.println(playController.attemptPets);
            });
//            attemptColor++;
            boolean wordGuessed = false;

            new Thread(() -> {
                while (true) {
                    try {
                        Object receivedObject = in.readObject();

                        if (receivedObject instanceof ArrayList) {
                            ArrayList<Character> receivedList = (ArrayList<Character>) receivedObject;

                            for (int i=0; i<lengthOfWord ; i++) {

                                if((receivedList.get(i) != secretWord.get(i)) && secretWord.get(i) == '_'
                                        && receivedList.get(i) != '#' ){
                                    secretWord.set(i,receivedList.get(i));
                                    System.out.println(receivedList.get(i));
                                }

                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

//                            Platform.runLater(()->{
                                playController.wordDisplay.setText(stringBuilder.toString());

                                if(!stringBuilder.toString().contains("_")){
                                    Platform.runLater(()->{
                                    cat2.setDisable(true);

                                    Platform.runLater(()->switchToPlayNew(currentStage,2));
                                });
                                    break;
                                }
//                            });


                        } else if (receivedObject instanceof Integer){
                            lengthOfWord = (Integer)receivedObject;

                            for (int i=0; i<lengthOfWord ; i++){
                                secretWord.add('_');
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

                            playController.wordDisplay.setText(stringBuilder.toString());


                        } else  {

                            numGuess.incrementAndGet();
                            StringBuilder drawCharacter = new StringBuilder();
                            if (numGuess.get()==1){
                                drawCharacter.append("\\");

                            } else if (numGuess.get()==2) {
                                drawCharacter.append("\\0");
                            } else if (numGuess.get()==3) {
                                drawCharacter.append("\\0/");
                            } else if (numGuess.get()==4) {
                                drawCharacter.append("\\0/\n  |");
                            } else if (numGuess.get()==5) {
                                drawCharacter.append("\\0/\n  |\n/");
                            }
                            if(numGuess.get() == 6){
                                if (playController.attemptPets == 3){
                                    gameOverPop();
                                    Platform.runLater(()->resetGame(currentStage));
                                    break;
                                }
                                drawCharacter.append("\\0/\n  |\n/ \\");
                                wrongGuessPop(3- playController.attemptPets);
                                Platform.runLater(()->switchToPlayNew(currentStage,-1));
                                break;
                            }

                            playController.characterDraw.setText(drawCharacter.toString());
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }



                    if(wordDisplay!= null){
                        System.out.println("here");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Character line : secretWord) {
                            stringBuilder.append(line);
                        }

                        playController.wordDisplay.setText(stringBuilder.toString());
//                        System.out.println(wordDisplay);
                    }

                }
            }).start();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void switchToPlay3(ActionEvent e) throws IOException{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/hangman.fxml"));
            Parent root = loader.load();
            Scene anotherScene = new Scene(root, 500, 500);
            Controller playController = loader.getController();
            playController.setOutputStream(out);
            playController.setInputStream(in);
            send("3");
            Stage currentStage = (Stage) cat3.getScene().getWindow();

            currentStage.setScene(anotherScene);
            anotherScene.getStylesheets().add("/Style/hangmanStyle.css");

            Platform.runLater(()-> {
                playController.attemptStates = playController.attemptStates +1;
                System.out.println(playController.attemptStates);
            });
//            attemptColor++;
            boolean wordGuessed = false;

            new Thread(() -> {
                while (true) {
                    try {
                        Object receivedObject = in.readObject();

                        if (receivedObject instanceof ArrayList) {
                            ArrayList<Character> receivedList = (ArrayList<Character>) receivedObject;

                            for (int i=0; i<lengthOfWord ; i++) {

                                if((receivedList.get(i) != secretWord.get(i)) && secretWord.get(i) == '_'
                                        && receivedList.get(i) != '#' ){
                                    secretWord.set(i,receivedList.get(i));
                                    System.out.println(receivedList.get(i));
                                }

                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

//                            Platform.runLater(()->{
                                playController.wordDisplay.setText(stringBuilder.toString());

                                if(!stringBuilder.toString().contains("_")){
                                    Platform.runLater(()->{
                                    cat3.setDisable(true);
                                    Platform.runLater(()->switchToPlayNew(currentStage,3));
                                    });
                                    break;
                                }
//                            });


                        } else if (receivedObject instanceof Integer){
                            lengthOfWord = (Integer)receivedObject;

                            for (int i=0; i<lengthOfWord ; i++){
                                secretWord.add('_');
                            }

                            StringBuilder stringBuilder = new StringBuilder();
                            for (Character line : secretWord) {
                                stringBuilder.append(line).append(" ");
                            }

                            playController.wordDisplay.setText(stringBuilder.toString());


                        } else  {

                            numGuess.incrementAndGet();
                            StringBuilder drawCharacter = new StringBuilder();
                            if (numGuess.get()==1){
                                drawCharacter.append("\\");

                            } else if (numGuess.get()==2) {
                                drawCharacter.append("\\0");
                            } else if (numGuess.get()==3) {
                                drawCharacter.append("\\0/");
                            } else if (numGuess.get()==4) {
                                drawCharacter.append("\\0/\n  |");
                            } else if (numGuess.get()==5) {
                                drawCharacter.append("\\0/\n  |\n/");
                            }
                            if(numGuess.get() == 6){
                                if (playController.attemptStates == 3){
                                    gameOverPop();
                                    Platform.runLater(()->resetGame(currentStage));
                                    break;
                                }
                                drawCharacter.append("\\0/\n  |\n/ \\");
                                wrongGuessPop(3- playController.attemptStates);
                                Platform.runLater(()->switchToPlayNew(currentStage,-1));
                                break;
                            }
                            playController.characterDraw.setText(drawCharacter.toString());
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }



                    if(wordDisplay!= null){
//                        System.out.println("here");
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Character line : secretWord) {
                            stringBuilder.append(line);
                        }

                        playController.wordDisplay.setText(stringBuilder.toString());
//                        System.out.println(wordDisplay);
                    }

                }
            }).start();


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void endGame (ActionEvent event) {
        System.exit(0);
    }

    public void closeModal (ActionEvent event) {
        gameOverPopupStage.close();
    }





    public void alphabetHandle(ActionEvent e){

        if (e.getSource() instanceof Button) {
            Button clickedButton = (Button) e.getSource();
            String letter = clickedButton.getText();
            send(letter);
            clickedButton.setDisable(true);
        }

    }

public void send(String data) {

    try {
        out.writeObject(data);
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}


}
