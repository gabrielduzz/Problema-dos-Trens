/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/08/2023
* Ultima alteracao: 02/09/2023
* Nome: PrincipalController.java
* Funcao: Gerenciador dos metodos e das threads
********************************************************************/

package control;

import java.util.ResourceBundle;
import java.net.URL;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.TrainThread;


public class PrincipalController implements Initializable{

  //referencia os elementos da cena presentes no arquivo FXML
  @FXML private AnchorPane primaryAnchorPane;
  @FXML private ImageView trails;
  @FXML private ImageView train1Img;
  @FXML private ImageView train2Img;
  @FXML private Slider sliderTrain1;
  @FXML private Label labelTrain1;
  @FXML private Rectangle greenRectangle;
  @FXML private Text speedMeasurement1;
  @FXML private Slider sliderTrain2;
  @FXML private Label labelTrain2;
  @FXML private Rectangle blueRectangle;
  @FXML private Text speedMeasurement2;
  @FXML private ImageView positionMarker1;
  @FXML private ImageView positionMarker2;
  @FXML private ImageView positionMarker3;
  @FXML private ImageView positionMarker4;
  @FXML private Rectangle choosingRectangle;
  @FXML private ImageView resetButton;
  @FXML private ImageView pauseButton;
  @FXML private ImageView resumeButton;
  @FXML private ImageView choosingTitle1;
  @FXML private ImageView choosingTitle2;
  @FXML private ImageView speedTitle1;
  @FXML private ImageView speedTitle2;
  @FXML private Rectangle opacityRectangle;
  @FXML private ImageView lockVariableButton;
  @FXML private ImageView strictAlternationButton;
  @FXML private ImageView petersonSolutionButton;
  @FXML private ImageView mutualExclusionMethodTitle;

  private TrainThread train1;
  private TrainThread train2;

  private int initialPositionTrain1;
  private int initialPositionTrain2;
 
  private boolean choosingPhase = false; //variavel que define qual dos trens esta sendo posicionado
  private boolean threadsStarted = false; //variavel qu define se a thread dos trens ja foi inicializada

  /********************************************************************
  * Metodo: setLockVariable
  * Funcao: configurar o método de exclusao mutua como variavel de
            travamento
  * Parametros: event = MouseEvent, click do mouse sobre o botao
  * Retorno: void
  ********************************************************************/
  @FXML
  void setLockVariable(MouseEvent event) {
    TrainThread.setMutualExclusionMethod(0);
    changeScene1();
  } //fim do metodo setLockVariable

  /********************************************************************
  * Metodo: setStrictAlternation
  * Funcao: configurar o método de exclusao mutua como estrita
            alternancia
  * Parametros: event = MouseEvent, click do mouse sobre o botao
  * Retorno: void
  ********************************************************************/
  @FXML
  void setStrictAlternation(MouseEvent event) {
    TrainThread.setMutualExclusionMethod(1);
    changeScene1();
  } //fim do metodo setStrictAlternation

  /********************************************************************
  * Metodo: setPetersonSolution
  * Funcao: configurar o metodo de exclusao mutua como solucao de 
            peterson
  * Parametros: event = MouseEvent, click do mouse sobre o botao
  * Retorno: void
  ********************************************************************/
  @FXML
  void setPetersonSolution(MouseEvent event) {
    TrainThread.setMutualExclusionMethod(2);
    changeScene1();
  } //fim do metodo setPetersonSolution

  /********************************************************************
  * Metodo: changeScene1
  * Funcao: manipula os elementos presentes na tela atual para simular
            uma troca de cena 
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  private void changeScene1(){
    /*na cena atual existem duas camadas de elementos, uma com os objetos da tela de escolha do metodo 
    de exclusao mutua e outra com os da tela de escolha da posicao dos trens
    esse metodo define quais elementos devem ser removidos da tela de escolha das posicoes e quais devem
    estar visiveis e habilitados para a proxima tela */
    opacityRectangle.setVisible(false);
    lockVariableButton.setVisible(false);
    lockVariableButton.setDisable(true);
    strictAlternationButton.setVisible(false);
    strictAlternationButton.setDisable(true);
    petersonSolutionButton.setVisible(false);
    petersonSolutionButton.setDisable(true);
    mutualExclusionMethodTitle.setVisible(false);
    choosingTitle1.setVisible(true);
    choosingRectangle.setVisible(true);
    choosingRectangle.setFill(Color.web("#1b4e3c"));
    positionMarker1.setVisible(true);
    positionMarker1.setDisable(false);
    positionMarker2.setVisible(true);
    positionMarker2.setDisable(false);
    positionMarker3.setVisible(true);
    positionMarker3.setDisable(false);
    positionMarker4.setVisible(true);
    positionMarker4.setDisable(false);
  } //fim do metodo changeScene1

  /********************************************************************
  * Metodos: choosePosition(1, 2, 3, 4)
  * Funcao: configurar a posicao inicial dos trens por meio de eventos na
            tela de escolha do posicionamento
  * Parametros: event = MouseEvent, click do mouse sobre determinado
                marcador
  * Retorno: void
  ********************************************************************/
  @FXML
  void choosePosition1(MouseEvent event) {
    /* checa em qual fase do posicionamento esta, caso a variavel choosingPhase esteja com valor false
    posiciona o trem 1 na posicao 1 (cima esquerda), caso contrario, se seu valor for true, posiciona o trem 2
    nessa mesma posicao */
    if(!choosingPhase){
      initialPositionTrain1 = 1; //guarda a posicao inicial do trem 1 (cima esquerda)
      train1.setInitialPosition(initialPositionTrain1);
      //deixa invisivel e desabilita o marcador 1 e o marcador que esta na direcao oposta desse
      positionMarker1.setVisible(false);
      positionMarker1.setDisable(true);
      positionMarker3.setVisible(false);
      positionMarker3.setDisable(true);

      //posiciona o trem no local correto
      train1Img.setLayoutX(160);
      train1Img.setLayoutY(-17);
      train1Img.setRotate(90);
      train1Img.setScaleY(1);
      train1Img.setVisible(true);

      //troca alguns elementos da cena e prepara a escolha do posicionamento do trem 2 
      choosingRectangle.setFill(Color.web("#011627"));
      choosingTitle1.setVisible(false);
      choosingTitle2.setVisible(true);
      choosingPhase = true; //indica que agora o trem a ser posicionado eh o 2
      return;
    } else {
      initialPositionTrain2 = 1;  //guarda a posicao inicial do trem 2 (cima esquerda)
      train2.setInitialPosition(initialPositionTrain2);
      //deixa invisivel e desabilita o marcador 1 e o marcador que esta na direcao oposta desse
      positionMarker1.setVisible(false);
      positionMarker1.setDisable(true);
      positionMarker3.setVisible(false);
      positionMarker3.setDisable(true);

      //posiciona o trem no local correto
      train2Img.setLayoutX(160);
      train2Img.setLayoutY(-17);
      train2Img.setRotate(90);
      train2Img.setScaleY(1);
      train2Img.setVisible(true);

      train1.setAnimationStarted(true); //indica que a animacao de movimento dos trens pode ser iniciada
      train2.setAnimationStarted(true);
      choosingPhase = false;  //indica que, caso haja o reinicio da simulacao, o trem a ser reposicionado eh o 1
      changeScene2(); //troca para a cena em que ha a simulacao dos trens (metodo changeScene2 sera explicado na linha ...)
      TrainThread.setTurn1(1);
      TrainThread.setTurn2(0);
      if(!threadsStarted){
        train1.start();
        train2.start();
        threadsStarted = true;
      }
      return;
    }
  } //fim do metodo choosePosition1

  @FXML
  void choosePosition2(MouseEvent event) {
     /* checa em qual fase do posicionamento esta, caso a variavel choosingPhase esteja com valor false
    posiciona o trem 1 na posicao 2 (cima direita), caso contrario, se seu valor for true, posiciona o trem 2
    nessa mesma posicao */
    if(!choosingPhase){
      initialPositionTrain1 = 2; //guarda a posicao inicial do trem 1 (cima direita)
      train1.setInitialPosition(initialPositionTrain1);
      //deixa invisivel e desabilita o marcador 2 e o marcador que esta na direcao oposta desse
      positionMarker2.setVisible(false);
      positionMarker2.setDisable(true);
      positionMarker4.setVisible(false);
      positionMarker4.setDisable(true);

      //posiciona o trem no local correto
      train1Img.setLayoutX(316);
      train1Img.setLayoutY(-17);
      train1Img.setRotate(90);
      train1Img.setScaleY(1);
      train1Img.setVisible(true);

      //troca alguns elementos da cena e prepara a escolha do posicionamento do trem 2 
      choosingRectangle.setFill(Color.web("#011627"));
      choosingTitle1.setVisible(false);
      choosingTitle2.setVisible(true);
      choosingPhase = true; //indica que agora o trem a ser posicionado eh o 2
      return;
    } else {
      initialPositionTrain2 = 2;  //guarda a posicao inicial do trem 2 (cima direita)
      train2.setInitialPosition(initialPositionTrain2);
      //deixa invisivel e desabilita o marcador 2 e o marcador que esta na direcao oposta desse
      positionMarker2.setVisible(false);
      positionMarker2.setDisable(true);
      positionMarker4.setVisible(false);
      positionMarker4.setDisable(true);

      //posiciona o trem no local correto
      train2Img.setLayoutX(316);
      train2Img.setLayoutY(-17);
      train2Img.setRotate(90);
      train2Img.setScaleY(1);
      train2Img.setVisible(true);

      train1.setAnimationStarted(true); //indica que a animacao de movimento dos trens pode ser iniciada
      train2.setAnimationStarted(true);
      choosingPhase = false;  //indica que, caso haja o reinicio da simulacao, o trem a ser reposicionado eh o 1
      changeScene2(); //troca para a cena em que ha a simulacao dos trens (metodo changeScene2 sera explicado na linha ...)
      TrainThread.setTurn1(1);
      TrainThread.setTurn2(0);
      if(!threadsStarted){
        train1.start();
        train2.start();
        threadsStarted = true;
      }
      return;
    }
  } //fim do metodo choosePosition2

  @FXML
  void choosePosition3(MouseEvent event) {
     /* checa em qual fase do posicionamento esta, caso a variavel choosingPhase esteja com valor false
    posiciona o trem 1 na posicao 3 (baixo esquerda), caso contrario, se seu valor for true, posiciona o trem 2
    nessa mesma posicao */
    if(!choosingPhase){
      initialPositionTrain1 = 3; //guarda a posicao inicial do trem 1 (baixo esquerda)
      train1.setInitialPosition(initialPositionTrain1);
      //deixa invisivel e desabilita o marcador 3 e o marcador que esta na direcao oposta desse
      positionMarker1.setVisible(false);
      positionMarker1.setDisable(true);
      positionMarker3.setVisible(false);
      positionMarker3.setDisable(true);

      //posiciona o trem no local correto
      train1Img.setLayoutX(160);
      train1Img.setLayoutY(661);
      train1Img.setRotate(270);
      train1Img.setScaleY(1);
      train1Img.setVisible(true);

      //troca alguns elementos da cena e prepara a escolha do posicionamento do trem 2 
      choosingRectangle.setFill(Color.web("#011627"));
      choosingTitle1.setVisible(false);
      choosingTitle2.setVisible(true);
      choosingPhase = true; //indica que agora o trem a ser posicionado eh o 2
      return;
    } else {
      initialPositionTrain2 = 3;  //guarda a posicao inicial do trem 2 (baixo esquerda)
      train2.setInitialPosition(initialPositionTrain2);
      //deixa invisivel e desabilita o marcador 3 e o marcador que esta na direcao oposta desse
      positionMarker1.setVisible(false);
      positionMarker1.setDisable(true);
      positionMarker3.setVisible(false);
      positionMarker3.setDisable(true);

      //posiciona o trem no local correto
      train2Img.setLayoutX(160);
      train2Img.setLayoutY(661);
      train2Img.setRotate(270);
      train2Img.setScaleY(1);
      train2Img.setVisible(true);

      train1.setAnimationStarted(true); //indica que a animacao de movimento dos trens pode ser iniciada
      train2.setAnimationStarted(true);
      choosingPhase = false;  //indica que, caso haja o reinicio da simulacao, o trem a ser reposicionado eh o 1
      changeScene2(); //troca para a cena em que ha a simulacao dos trens (metodo changeScene2 sera explicado na linha ...)
      TrainThread.setTurn2(1);
      TrainThread.setTurn1(0);
      if(!threadsStarted){
        train1.start();
        train2.start();
        threadsStarted = true;
      }
      return;
    }
  } //fim do metodo choosePosition3

  @FXML
  void choosePosition4(MouseEvent event) {
     /* checa em qual fase do posicionamento esta, caso a variavel choosingPhase esteja com valor false
    posiciona o trem 1 na posicao 4 (baixo direita), caso contrario, se seu valor for true, posiciona o trem 2
    nessa mesma posicao */
    if(!choosingPhase){
      initialPositionTrain1 = 4; //guarda a posicao inicial do trem 1 (baixo direita)
      train1.setInitialPosition(initialPositionTrain1);
      //deixa invisivel e desabilita o marcador 4 e o marcador que esta na direcao oposta desse
      positionMarker2.setVisible(false);
      positionMarker2.setDisable(true);
      positionMarker4.setVisible(false);
      positionMarker4.setDisable(true);

      //posiciona o trem no local correto
      train1Img.setLayoutX(316);
      train1Img.setLayoutY(661);
      train1Img.setRotate(270);
      train1Img.setScaleY(-1);
      train1Img.setVisible(true);

      //troca alguns elementos da cena e prepara a escolha do posicionamento do trem 2 
      choosingRectangle.setFill(Color.web("#011627"));
      choosingTitle1.setVisible(false);
      choosingTitle2.setVisible(true);
      choosingPhase = true; //indica que agora o trem a ser posicionado eh o 2
      return;
    } else {
      initialPositionTrain2 = 4;  //guarda a posicao inicial do trem 2 (baixo direita)
      train2.setInitialPosition(initialPositionTrain2);
      //deixa invisivel e desabilita o marcador 4 e o marcador que esta na direcao oposta desse
      positionMarker2.setVisible(false);
      positionMarker2.setDisable(true);
      positionMarker4.setVisible(false);
      positionMarker4.setDisable(true);

      //posiciona o trem no local correto
      train2Img.setLayoutX(316);
      train2Img.setLayoutY(661);
      train2Img.setRotate(270);
      train2Img.setScaleY(-1);
      train2Img.setVisible(true);

      train1.setAnimationStarted(true); //indica que a animacao de movimento dos trens pode ser iniciada
      train2.setAnimationStarted(true);
      choosingPhase = false;  //indica que, caso haja o reinicio da simulacao, o trem a ser reposicionado eh o 1
      changeScene2(); //troca para a cena em que ha a simulacao dos trens
      TrainThread.setTurn2(1);
      TrainThread.setTurn1(0);
      if(!threadsStarted){
        train1.start();
        train2.start();
        threadsStarted = true;
      }
      return;
    }
  } //fim do metodo choosePosition4

  /********************************************************************
  * Metodo: changeScene2
  * Funcao: manipula os elementos presentes na tela atual para simular
            uma troca de cena 
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  private void changeScene2(){
    /*na cena atual existem duas camadas de elementos, uma com os objetos da tela de posicionamento dos trens
    e outra com os da tela de movimento
    esse metodo define quais elementos devem ser removidos da tela de escolha das posicoes e quais devem
    estar visiveis e habilitados para a proxima tela */
    choosingTitle2.setVisible(false);
    choosingRectangle.setVisible(false);
    speedMeasurement1.setVisible(true);
    speedMeasurement2.setVisible(true);
    labelTrain1.setVisible(true);
    labelTrain2.setVisible(true);
    greenRectangle.setVisible(true);
    blueRectangle.setVisible(true);
    speedTitle1.setVisible(true);
    speedTitle2.setVisible(true);
    sliderTrain1.setVisible(true);
    sliderTrain2.setVisible(true);
    resetButton.setVisible(true);
    resetButton.setDisable(false);
    pauseButton.setVisible(true);
    pauseButton.setDisable(false);
  } //fim do metodo changeScene2
 

  /********************************************************************
  * Metodo: pause
  * Funcao: pausa a simulacao
  * Parametros: event = MouseEvent, click do mouse sobre o botao de pause
  * Retorno: void
  ********************************************************************/
  @FXML
  void pause(MouseEvent event) {
    //verifica se a simulacao ja esta pausada
    //caso nao esteja:
    if(!train1.getIsPaused() && !train2.getIsPaused()){
      //muda a variavel isPaused para verdadeira, o que para o movimento dos trens
      train1.setIsPaused(true);
      train2.setIsPaused(true);

      //troca o botao de pause para o de continuar
      pauseButton.setVisible(false);
      pauseButton.setDisable(true);
      resumeButton.setVisible(true);
      resumeButton.setDisable(false);

    //caso esteja
    } else {
      //muda a variavel isPaused para falso, o que continua o movimento dos trens
      train1.setIsPaused(false);
      train2.setIsPaused(false);

      //troca o botao de continuar para o de pause
      pauseButton.setVisible(true);
      pauseButton.setDisable(false);
      resumeButton.setVisible(false);
      resumeButton.setDisable(true);
    }   
  }

  /********************************************************************
  * Metodo: reset
  * Funcao: reinicia os objetos e volta para a tela de posicionamento
  * Parametros: event = MouseEvent, click do mouse sobre o botao de reset
  * Retorno: void
  ********************************************************************/
  @FXML
  void reset(MouseEvent event) {
    //muda o estado da variavel animationStarted para parar o movimento dos trens
    train1.setAnimationStarted(false);
    train2.setAnimationStarted(false);

    //manipula os elementos para voltarem a mostrar a tela de posicionamento
    train1Img.setVisible(false);
    train2Img.setVisible(false);
    speedMeasurement1.setVisible(false);
    speedMeasurement2.setVisible(false);
    sliderTrain1.setVisible(false);
    sliderTrain2.setVisible(false);
    greenRectangle.setVisible(false);
    blueRectangle.setVisible(false);
    speedTitle1.setVisible(false);
    speedTitle2.setVisible(false);
    labelTrain1.setVisible(false);
    labelTrain2.setVisible(false);
    resetButton.setVisible(false);
    resetButton.setDisable(true);
    pauseButton.setVisible(false);
    pauseButton.setDisable(true);
    resumeButton.setVisible(false);
    resumeButton.setDisable(true);
    opacityRectangle.setVisible(true);
    lockVariableButton.setVisible(true);
    lockVariableButton.setDisable(false);
    strictAlternationButton.setVisible(true);
    strictAlternationButton.setDisable(false);
    petersonSolutionButton.setVisible(true);
    petersonSolutionButton.setDisable(false);
    mutualExclusionMethodTitle.setVisible(true);

    //reinicia o index da movimentacao dos trens
    train1.setCurrentMovement(0);
    train2.setCurrentMovement(0);

    //volta ao padrao as velocidades dos trens
    sliderTrain1.setValue(50);
    sliderTrain2.setValue(50);
    labelTrain1.setText("50");
    labelTrain2.setText("50");

    //despausa caso o reset tenha sido solicitado durante um pause
    train1.setIsPaused(false);
    train2.setIsPaused(false);

    //reseta as variaveis de exclusao mutua
    TrainThread.setMutualExclusionMethod(-1);
    TrainThread.resetLockVariable();
    TrainThread.resetStrictAlternation();
    TrainThread.resetPetersonSolution();

    //reseta a variavel que controla o acesso de cada trem a regiao critica
    train1.resetAccess();
    train2.resetAccess();

  }

  @Override
  public void initialize(URL arg0, ResourceBundle rb){
    train1 = new TrainThread(train1Img, 0, sliderTrain1, labelTrain1); //instancia os trens
    train2 = new TrainThread(train2Img, 1, sliderTrain2, labelTrain2);
  }   
}



