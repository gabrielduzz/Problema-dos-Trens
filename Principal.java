/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/08/2023
* Ultima alteracao: 02/09/2023
* Nome: Principal.java
* Funcao: Carregar a cena inicial
********************************************************************/

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import control.PrincipalController;
import model.TrainThread;

public class Principal extends Application {

  /********************************************************************
  * Metodo: createContent
  * Funcao: carregar a tela principal da simulacao, apos o usuario 
            clicar no botao iniciar
  * Parametros: nenhum
  * Retorno: root2(tipo Parent) = os elementos da cena para ser carregada no stage principal, apos
             ser liberado o inicio da simulacao
  ********************************************************************/
  private Parent createContent() throws Exception{
    //carrega o arquivo FXML da simulacao
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/principal_view.fxml"));
    //atribui ao elemento principal da cena e o retorna
    Pane root3 = loader.load();
    return root3;
  } //fim do metodo createContent


  /********************************************************************
  * Metodo: start
  * Funcao: metodo padrao que tem a funcao de definir o container da 
            aplicacao
  * Parametros: primaryStage = janela principal
  * Retorno: void
  ********************************************************************/
  @Override
  public void start(Stage primaryStage) throws Exception {
    //cria o elemento principal da cena inicial
    Pane root1 = new Pane();
    root1.setPrefSize(602, 683);

    //cria os elementos da tela de iniciar
    //plano de fundo
    Image backgroundImage = new Image("/resources/tela de inicio.png");
    ImageView background = new ImageView(backgroundImage);
    background.setImage(backgroundImage);
    background.setFitHeight(683);
    background.setFitWidth(602);
    

    //botao de iniciar
    Image startButtonImage = new Image("/resources/iniciar.png");
    ImageView startButton = new ImageView(startButtonImage);
    startButton.setImage(startButtonImage);
    startButton.setFitHeight(118);
    startButton.setFitWidth(294);
    startButton.setLayoutX(154);
    startButton.setLayoutY(570);

    //adiciona ao fundo principal
    root1.getChildren().addAll(background, startButton);

    //instancia a cena inicial(scene1) e a cena da simulacao(scene2)
    Scene scene1 = new Scene(root1);
    Scene scene2 = new Scene(createContent());

    //define o evento de click do mouse no botao de iniciar
    startButton.setOnMouseClicked(event -> {
      primaryStage.setScene(scene2); //quando esse evento ocorrer, sera trocada a tela
    });

    //define alguns detalhes do container principal
    primaryStage.setTitle("Lokomotivas");
    primaryStage.setScene(scene1);
    primaryStage.setResizable(false);
    primaryStage.show();
  }//fim do metodo start

  public static void main(String[] args) {
    launch(args);
  }

 
}