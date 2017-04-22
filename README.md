# play-with-robots
Support software to build my robot toy using android (kotlin) and arduino (c++)

For the no portuguese speakers sory, but to be fluent in my writing all the other things will be writed in portuguese (except the code itself)

O objectivo do projecto está descrito [aqui](https://github.com/jpmoreto/play-with-robots/blob/master/docs/descri%C3%A7%C3%A3o_robot.pdf)

O projecto está dividido nas seguintes pastas:
* android/src/app : Código que só corre em ambiente android, tipicamente o UI e as comunicações bluetooth. O package jpm/lib é um link para o mesmo código existente na biblioteca lib, e esse link foi criado, em vez de ter uma dependencia para a biblioteca para o progard poder remover o código não utilizado de forma eficiente (lib depende de outras bibliotecas externas).
* andoroid/src/lib : Codigo independente da plataforma android, onde estão os algoritmos de caminho mais curto, as representação do mapa do espaço ocupado, ...
* android/src/testsApp : Código usado só para testar os algoritmos em lib fora da plataforma android (é mais simples). Usa o javafx para visualizar os outputs dos algoritmos e ajudar a perceber se a coisa está a funcionar conforme o pretendido.
