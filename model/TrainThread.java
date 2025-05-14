/********************************************************************
* Autor: Gabriel dos Santos
* Inicio: 21/08/2023
* Ultima alteracao: 02/09/2023
* Nome: TrainThread.java
* Funcao: Manipular os trens
********************************************************************/

package model;

import java.lang.Thread;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import control.PrincipalController;

public class TrainThread extends Thread{
  
  private int initialPosition;
  //variavel que guarda a parte do movimento em que esta o trem
  private int currentMovement = 0;
  private ImageView train;
  private int id;

  private boolean isPaused = false; //variavel que define se a simulacao esta pausada
  private boolean animationStarted = false; //variavel que define se os dois trens ja foram posicionados
  private AnimationTimer timer; //instancia o objeto que permite fazer a animacao dos trens
  private Slider sliderTrain;
  private Label labelTrain;

  //variaveis correspondentes ao metodo da variavel de travamento
  private static boolean lockVariable = false; 
  private static int mutex1 = 1; //variavel de travamento da regiao critica 1 (cima)
  private static int mutex2 = 1; //variavel de travamento da regiao critica 2(baixo)
  private boolean hasAccess1 = false; //atributo que define se o trem ja tem acesso a regiao critica
  private boolean hasAccess2 = false;

  //variaveis correspondentes ao metodo da estrita alternancia
  private static boolean strictAlternation = false;
  private static int turn1; //variavel de turno da regiao critica 1
  private static int turn2; //variavel de turno da regiao critica 2

  //variaveis correspondentes ao metodo da solucao de peterson
  private static boolean petersonSolution = false;
  private static boolean[] interest1 = new boolean[2]; //array de interesse na regiao critica 1
  private static boolean[] interest2 = new boolean[2]; //array de interesse na regiao critica 2

  public TrainThread(ImageView train, int id, Slider sliderTrain, Label labelTrain){
    this.train = train;
    this.id = id;
    this.sliderTrain = sliderTrain;
    this.labelTrain = labelTrain;
  }

  //setters e getters
  public static void setTurn1(int turn){
    turn1 = turn;
  }

  public static void setTurn2(int turn){
    turn2 = turn;
  }

  public void setInitialPosition(int initialPosition){
    this.initialPosition = initialPosition;
  } 

  public void setAnimationStarted(boolean animationStarted){
    this.animationStarted = animationStarted;
  }

  public void setIsPaused(boolean isPaused){
    this.isPaused = isPaused;
  }

  public boolean getIsPaused(){
    return isPaused;
  }

  public void setCurrentMovement(int currentMovement){
    this.currentMovement = currentMovement;
  }

  public static void setMutualExclusionMethod(int method){
    if(method == 0){
      lockVariable = true;
    } else if (method == 1){
      strictAlternation = true;
    } else if (method == 2) {
      petersonSolution = true;
    } else {
      lockVariable = false;
      strictAlternation = false;
      petersonSolution = false;
    }
  }

  //metodos que resetam as variaveis de cada metodo de exclusao mutua
  public void resetAccess(){
    hasAccess1 = false;
    hasAccess2 = false;
  }

  public static void resetLockVariable(){
    mutex1 = 1;
    mutex2 = 1;
  }

  public static void resetStrictAlternation(){
    turn1 = 1;
    turn2 = 1;
  }

  public static void resetPetersonSolution(){
    for(int i = 0; i < 2; i++){
      interest1[i] = false;
    }
    for(int i = 0; i < 2; i++){
      interest2[i] = false;
    }

    turn1 = 1;
    turn2 = 1;
  }
  

  /**********************************************************************
  * Metodo: moveTo
  * Funcao: realiza o movimento do trem especificado ate um determinado
            pixel definido nos parametros
  * Parametros: train = o trem que sera movimentado; x = coordenada x do
                em que o movimento deve ser finalizado; y = coordenada y
                do ponto em que o movimento deve ser finalizado;
                elapsedSeconds = o tempo que se passou desde a ultima 
                chamada do timer; speed = velocidade do trem especificado
  * Retorno: void
  ***********************************************************************/
  private void moveTo(double x, double y, double elapsedSeconds, double speed){
    /*calcula o coeficiente angular da reta que passa entre o ponto em que o trem 
    esta atualmente e o ponto em que o movimento deve ser finalizado */
    double angularCoefficient = (y - train.getLayoutY())/(x - train.getLayoutX());
    /*como definido na matematica, o coeficiente angular de uma reta eh a tangente do angulo
    entre essa e o eixo x, portanto, calcula-se esse angulo pela funcao arco tangente (atan)
    e transforma o resultado de radianos para graus, que sera o angulo de rotacao do trem*/
    double degrees = Math.toDegrees(Math.atan(angularCoefficient));

      /*compara a coordenada x de ambos os pontos, caso a diferenca seja menor que 2, a reta esta
      em um angulo muito proximo de 90 graus, o que necessita de um tratamento especial */
      //caso a diferenca seja maior que 2:
      if(Math.abs(x - train.getLayoutX()) > 2){
        //verifica se o trem esta na parte de cima
        //caso o trem esteja em cima:
        if(initialPosition == 1 || initialPosition == 2){
          /*verica qual a relacao da posicao atual do trem com a coordenada especificada e confere 
          se essa coordenada ja foi ultrapassada*/
          //caso o trem esteja a esquerda e acima do ponto definido:
          if(train.getLayoutX() < x && train.getLayoutY() < y){
            //ajusta a rotacao do trem de acordo com o calculo feito no inicio do metodo
            train.setRotate(degrees);
            /*move o trem com base na funcao horaria do espaco, s = v/t, onde v eh
            a velocidade do trem definida pelo parametro speed (velocidade do trem em pixels 
            por segundo), t eh o tempo definido pelo parametro elapsedSeconds (o tempo desde a ultima chamada)
            e s sera a quantidade de pixels que o trem deve mover para obedecer a velocidade escolhida pelo
            usuario */
            //faz o calculo descrito acima e aplica na coordenada x
            train.setLayoutX(train.getLayoutX() + elapsedSeconds*speed); 
            //pega o resultado do calculo e multiplica pelo coeficiente linear da reta entre os dois pontos,
            //para que o trem siga essa reta com perfeicao
            train.setLayoutY(train.getLayoutY() + angularCoefficient*elapsedSeconds*speed); 

          //caso o trem esteja a direita e acima do ponto definido
          } else if(train.getLayoutX() > x && train.getLayoutY() < y){
            //repete os processos descritos acima
            train.setRotate(180 + degrees);
            train.setLayoutX(train.getLayoutX() - elapsedSeconds*speed);
            train.setLayoutY(train.getLayoutY() - angularCoefficient*elapsedSeconds*speed); 

          //caso o ponto definido ja tenha sido alcancado
          } else 
            //define que o movimento do trem foi finalizado e pode seguir para o proximo
            currentMovement++;

        //caso o trem esteja embaixo:
        } else {
          /*verica qual a relacao da posicao atual do trem com a coordenada especificada e confere 
          se essa coordenada ja foi ultrapassada*/
          //caso o trem esteja a esquerda e abaixo do ponto:
          if(train.getLayoutX() < x && train.getLayoutY() > y){
            train.setRotate(360 + degrees);
            train.setLayoutX(train.getLayoutX() + elapsedSeconds*speed);
            train.setLayoutY(train.getLayoutY() + angularCoefficient*elapsedSeconds*speed); 

          //caso o trem esteja a direita e abaixo do ponto:
          } else if(train.getLayoutX() > x && train.getLayoutY() > y){
            train.setRotate(180 + degrees);
            train.setLayoutX(train.getLayoutX() - elapsedSeconds*speed);
            train.setLayoutY(train.getLayoutY() - angularCoefficient*elapsedSeconds*speed); 

          //caso o ponto definido ja tenha sido alcancado
          } else {
            currentMovement++;
          }   
        }
      //caso a diferenca entre as coordenadas x seja menor que 2, ou seja, a reta eh proxima a 90 graus:
      } else {
        //verifica se o trem esta na parte de cima
        //caso o trem esteja em cima:
        if(initialPosition == 1 || initialPosition == 2){
          //verifica se o trem ja alcancou o ponto definido
          //caso nao tenha:
          if(train.getLayoutY() < y){
            train.setRotate(90);
            train.setLayoutY(train.getLayoutY() + elapsedSeconds*speed); 
          //caso tenha: 
          } else {
            currentMovement++;
          }
        
        //caso o trem esteja embaixo:
        } else {
          if(train.getLayoutY() > y){
            train.setRotate(270);
            train.setLayoutY(train.getLayoutY() - elapsedSeconds*speed); 
          } else {
          currentMovement++;
          }
        }  
      }

  } //fim do metodo moveTo

  /*as linhas a seguir definem uma estrutura denominada "adapter", onde eh criada uma interface com um
  metodo que pode ser sobrescrito por qualquer objeto que a implemente. Dessa forma eh possivel que cada 
  objeto chame o metodo padrao de sua interface com parametros diferentes */
  public interface MoveAction {
    void move(double elapsedSeconds, double speed);
  }

  //isso tambem permite a criacao de outra estrutura que atua como um array de funcoes
  /*os quatro vetores abaixo contem as instrucoes para movimentacao dos trens em cada uma das
  posicoes possiveis, funcionando como arrays de movimento */
  private MoveAction[] moveActionsPosition1 = new MoveAction[] {
    /*para cada um dos movimentos necessarios para completar o trajeto definido, eh instanciado um
    objeto do tipo MoveAction, que sobrescreve o metodo padrao de sua interface usando a funcao moveTo
    como corpo do metodo e passando os parametros necessarios, como o ponto ate onde o trem deve mover

    assim, cada movimento tem sua indexacao e, com o auxilio de outras variaveis e metodos, serao chamados um por vez
    quando for necessario */

    //movimentacao da posicao 1 (cima esquerda)
    new MoveAction() { public void move(double elapsedSeconds, double speed) { 
      moveTo(160, 62, elapsedSeconds, speed);  } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 114, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 199, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 242, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 372, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 418, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 511, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 552, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 715, elapsedSeconds, speed);   } }
  };

  private MoveAction[] moveActionsPosition2 = new MoveAction[] {
    //movimentacao da posicao 2 (cima direita)
    new MoveAction() { public void move(double elapsedSeconds, double speed) { 
      moveTo(316, 65, elapsedSeconds, speed);  } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 114, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 199, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 242, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 374, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 418, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 511, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 552, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 715, elapsedSeconds, speed);   } }
  };

  private MoveAction[] moveActionsPosition3 = new MoveAction[] {
    //movimentacao da posicao 3 (baixo esquerda)
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 552, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 511, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 418, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 374, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 242, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 199, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 114, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) { 
      moveTo(160, 62, elapsedSeconds, speed);  } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, -78, elapsedSeconds, speed);   } }
  };

  private MoveAction[] moveActionsPosition4 = new MoveAction[] {
    //movimentacao da posicao 4 (baixo direita)
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 552, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 511, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 418, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 374, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(160, 242, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 199, elapsedSeconds, speed);   } },
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(238, 114, elapsedSeconds, speed);   } },   
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, 65, elapsedSeconds, speed);   } }, 
    new MoveAction() { public void move(double elapsedSeconds, double speed) {
      moveTo(316, -78, elapsedSeconds, speed);   } }
  };
  //fim dos arrays de movimentacao

  /**********************************************************************
  * Metodo: enterCriticalRegion1
  * Funcao: controlar o acesso a regiao critica utilizando o metodo de exclusao 
            mutua selecionado pelo usuario
  * Parametros: process(int), id do processo que quer entrar na regiao critica
  * Retorno: boolean, true caso o trem possa prosseguir para a regiao critica
             e false caso nao possa 
  ***********************************************************************/
  public boolean enterCriticalRegion1(int process){
    //tratamento por variavel de travamento
    if(lockVariable){
      if(mutex1 == 0 && hasAccess1 == false){
        return false;
      } 
      if(hasAccess1 == false){
        mutex1--;
        hasAccess1 = true;
      }
      return true;
    }
    //tratamento por estrita alternancia
    if(strictAlternation){
      if(id == 1){
        if(turn1 != 1){
          return false;
        }
      } else {
        if(turn1 != 0){
          return false;
        }
      }
      hasAccess1 = true;
      return true;
    } 
    //tratamento pela solucao de peterson
    if(petersonSolution){
      int other;

      other = 1 - process;
      interest1[process] = true;
      turn1 = process;
      
      if(turn1 == process && interest1[other] == true && hasAccess1 == false){
        return false;
      }

      hasAccess1 = true;
      return true;
    }

    return false;
  } //fim do metodo enterCriticalRegion1

  /**********************************************************************
  * Metodo: enterCriticalRegion2
  * Funcao: controlar o acesso a regiao critica utilizando o metodo de exclusao 
            mutua selecionado pelo usuario
  * Parametros: process(int), id do processo que quer entrar na regiao critica
  * Retorno: boolean, true caso o trem possa prosseguir para a regiao critica
             e false caso nao possa 
  ***********************************************************************/
  public boolean enterCriticalRegion2(int process){
    //tratamento por variavel de travamento
    if(lockVariable){
      if(mutex2 == 0 && hasAccess2 == false){
        return false;
      } 
      if(hasAccess2 == false){
        mutex2--;
        hasAccess2 = true;
      }
      return true;
    }
    //tratamento por estrita alternancia
    if(strictAlternation){
      if(id == 1){
        if(turn2 != 1){
          return false;
        }
      } else {
        if(turn2 != 0){
          return false;
        }
      }
      hasAccess2 = true;
      return true;
    } 
    //tratamento pela solucao de peterson
    if(petersonSolution){
      int other;

      other = 1 - process;
      interest2[process] = true;
      turn2 = process;
      if(turn2 == process && interest2[other] == true && hasAccess2 == false){
        return false;
      }
      hasAccess2 = true;
      return true;
    }

    return false;
  } //fim do metodo enterCriticalRegion2

  /**********************************************************************
  * Metodo: leaveCriticalRegion
  * Funcao: libera o acesso a regiao critica para o outro trem
  * Parametros: process(int), id do processo que saiu da regiao critica
  * Retorno: false
  ***********************************************************************/
  public void leaveCriticalRegion(int process){
    //tratamento por variavel de travamento
    if(lockVariable){
      if(hasAccess1 == true){
        mutex1++;
        hasAccess1 = false;
      }
      if(hasAccess2 == true){
        mutex2++;
        hasAccess2 = false;
      }
    } 
    //tratamento por estrita alternancia
    if (strictAlternation){
      if(hasAccess1 == true){
        if(turn1 == 1){
          turn1 = 0;
        } else {
          turn1 = 1;
        }
        hasAccess1 = false;
      }
      if(hasAccess2 == true){
        if(turn2 == 1){
          turn2 = 0;
        } else {
          turn2 = 1;
        }
        hasAccess2 = false;
      }
    }
    //tratamento por solucao de peterson
    if(petersonSolution){
      if(hasAccess1 == true){
        interest1[process] = false;
        hasAccess1 = false;
      }
      if(hasAccess2 == true){
        interest2[process] = false;
        hasAccess2 = false;
      }
    }
  }

  
  /********************************************************************
  * Metodo: update
  * Funcao: executa a movimentacao dos dois trens a cada frame do timer
  * Parametros: elapsedSeconds = tempo que se passou desde a ultima
                chamada do timer; speedTrain = velocidade do trem 1
                speedTrain2 = velocidade do trem 2
  * Retorno: void
  ********************************************************************/
  public void update(double elapsedSeconds, double speedTrain){
    //verifica a posicao inicial do trem
    switch(initialPosition){
      //caso seja cima esquerda:
      case 1:{
      /*confere se o index do movimento atual do trem eh menor que o tamanho do 
      array de movimentacao */
        //caso seja menor:
        if(currentMovement <= moveActionsPosition1.length-1) {
        /*verifica se o trem esta em uma das regioes criticas */
          //caso esteja na regiao critica 1
          if(currentMovement >= 1 && currentMovement <= 3){
            //checa se o trem pode entrar na regiao critica
            if(!enterCriticalRegion1(id)){
              break; //quebra o laco caso nao possa
            } 
            //continua o movimento caso possa prosseguir
            moveActionsPosition1[currentMovement].move(elapsedSeconds, speedTrain);
          
          //caso esteja na regiao critica 2
          } else if(currentMovement >= 5 && currentMovement <= 7){
            if(!enterCriticalRegion2(id)){
              break;
            }
            moveActionsPosition1[currentMovement].move(elapsedSeconds, speedTrain);

          //caso nao esteja em nenhuma regiao critica
          } else {
            //checa se ele esta saindo de alguma regiao critica
            if(hasAccess1 || hasAccess2){
              leaveCriticalRegion(id);
            }
            //completa o movimento atual do trem
            moveActionsPosition1[currentMovement].move(elapsedSeconds, speedTrain);
          }
                
        //caso seja maior:
        } else {
          //isso significa que o trem ultrapassou a tela e deve voltar do outro lado
          train.setLayoutX(160);
          train.setLayoutY(-78);
          //reinicia o index do movimento
          currentMovement = 0;
        } 

        break;
      }
      //caso seja cima direita
      case 2:{
        if(currentMovement <= moveActionsPosition2.length-1) {
          if(currentMovement >= 1 && currentMovement <= 3){
            if(!enterCriticalRegion1(id)){
               break;
            }
            moveActionsPosition2[currentMovement].move(elapsedSeconds, speedTrain);

          } else if(currentMovement >= 5 && currentMovement <= 7){
            if(!enterCriticalRegion2(id)){
              break;
            }
            moveActionsPosition2[currentMovement].move(elapsedSeconds, speedTrain);

          } else {
            if(hasAccess1 || hasAccess2){
              leaveCriticalRegion(id);
            }
              
            //completa o movimento atual do trem
            moveActionsPosition2[currentMovement].move(elapsedSeconds, speedTrain);
          }
                
        //caso seja maior:
        } else {
          //isso significa que o trem ultrapassou a tela e deve voltar do outro lado
          train.setLayoutX(316);
          train.setLayoutY(-78);
          //reinicia o index do movimento
          currentMovement = 0;
        } 

        break;
      }
      //caso seja baixo esquerda
      case 3:{
        if(currentMovement <= moveActionsPosition1.length-1) {
          if(currentMovement >= 5 && currentMovement <= 7){
            if(!enterCriticalRegion1(id)){
              break;
            }
              
            moveActionsPosition3[currentMovement].move(elapsedSeconds, speedTrain);

          } else if(currentMovement >= 1 && currentMovement <= 3){
            if(!enterCriticalRegion2(id)){
              break;
            }
              
            moveActionsPosition3[currentMovement].move(elapsedSeconds, speedTrain);

          } else {
            if(hasAccess1 || hasAccess2){
              leaveCriticalRegion(id);
            }
              //completa o movimento atual do trem
            moveActionsPosition3[currentMovement].move(elapsedSeconds, speedTrain);
          }
                
        } else {
          train.setLayoutX(160);
          train.setLayoutY(720);
          currentMovement = 0;
        } 
            
        break;
      }
      //caso seja baixo direita
      case 4:{
        if(currentMovement <= moveActionsPosition1.length-1) {
          if(currentMovement >= 5 && currentMovement <= 7){
            if(!enterCriticalRegion1(id)){
              break;
            }
            moveActionsPosition4[currentMovement].move(elapsedSeconds, speedTrain);
          } else if(currentMovement >= 1 && currentMovement <= 3){
            if(!enterCriticalRegion2(id)){
              break;
            } 
            moveActionsPosition4[currentMovement].move(elapsedSeconds, speedTrain);
          } else {
            if(hasAccess1 || hasAccess2){
              leaveCriticalRegion(id);
            } 
            //completa o movimento atual do trem
            moveActionsPosition4[currentMovement].move(elapsedSeconds, speedTrain);
          }

        } else {
          train.setLayoutX(316);
          train.setLayoutY(720);
          currentMovement = 0;
        } 
  
        break;
      }
    }           
  }//fim do metodo update

  /********************************************************************
  * Metodo: movementAnimation
  * Funcao: permite criar uma animacao para movimentar os trens por meio
            da biblioteca AnimationTimer
  * Parametros: nenhum
  * Retorno: void
  ********************************************************************/
  public void movementAnimation(){
    /* cria um timer que acessa o tempo de execucao do processo e a cada intervalo 
    determinado executa um frame */
    timer = new AnimationTimer(){
      private long lastUpdate; //variavel que guarda o tempo da ultima chamada do timer

      /* cria as variaveis que armazenarao a velocidade dos trens, que sera medida em
      pixels por segundo */
      private double speedTrain;
    
      /* sobrescreve o metodo start, que eh padrao da biblioteca AnimationTimer, para inicializar
      a variavel lastUpdate com o tempo em nanossegundos do inicio do processo */
      @Override
      public void start() {
        lastUpdate = System.nanoTime();
        super.start();
      }

      /* o metodo handle, padrao da biblioteca AnimationTimer, contem as instrucoes do que deve ser 
      executado a cada frame e possui como parametro o tipo long "now", que representa o tempo atual, em 
      nanossegundos, de execucao do processo */
      @Override
      public void handle(long now) {
        /* a variavel elapsedNanoSeconds recebe a diferenca de tempo entre a ultima chamada do timer e o 
        tempo atual de execucao, em nanossegundos, enquanto a variavel elapsedSeconds recebe esse valor 
        transformado para segundos */
        long elapsedNanoSeconds = now - lastUpdate;
        double elapsedSeconds = elapsedNanoSeconds / 1_000_000_000.0;

        /* atribui o valor de cada slider a variavel de velocidade dos seus respectivos trens
        e muda o texto dos labels para acompanharem esses valores */
        speedTrain = sliderTrain.getValue();
        labelTrain.setText(Integer.toString((int)speedTrain));

        /* checa se o frame atual da animacao deve ser executado, ou seja, se os dois trens ja foram 
        posicionados (animationStarted) e se a animacao nao foi pausada (!isPaused) */
        if(animationStarted && !isPaused){
          //executa as instrucoes desse frame
          update(elapsedSeconds, speedTrain); 
        }

        //atualiza o tempo da ultima chamada do timer
        lastUpdate = now;
      }   
    };

    timer.start();
  } //fim do metodo movementAnimation

  public void run(){
    Platform.runLater(() -> {
      movementAnimation();
    });
   
  }
}
