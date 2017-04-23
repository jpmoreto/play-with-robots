# play-with-robots
Support software to build my robot toy using android (kotlin) and arduino (c++)

For the no portuguese speakers sory, but to be fluent in my writing all the other things will be writed in portuguese (except the code itself)

O objectivo do projecto está descrito [aqui](https://github.com/jpmoreto/play-with-robots/blob/master/docs/descri%C3%A7%C3%A3o_robot.pdf)
E algumas fotografias do robot (à uns meses atrás):
* [fotografia 1](https://github.com/jpmoreto/play-with-robots/blob/master/docs/20170208_013006.jpg)
* [fotografia 2](https://github.com/jpmoreto/play-with-robots/blob/master/docs/20170208_013019.jpg)

Este projecto está em obras e ainda falta muito trabalho quer de software quer com o ferro de soldar. Mas já existe algum código na pasta andoroid/src/lib que já está pronto a utilizar. 

O projecto está dividido nas seguintes pastas:
* **android/app** : Código que só corre em ambiente android, tipicamente o UI e as comunicações bluetooth. O package jpm/lib é um link para o mesmo código existente na biblioteca lib, e esse link foi criado em vez de ter uma dependencia para a biblioteca, para o progard poder remover o código não utilizado de forma eficiente (lib depende de outras bibliotecas externas).
* **android/lib** : Código independente da plataforma android, onde estão os algoritmos de caminho mais curto, a representação do mapa do espaço ocupado, ... O restante projecto é completamente especifico para o robot que estou a construir. O código existente em lib pode ser usado noutros contextos.
* **android/testsApp** : Código usado só para testar os algoritmos em lib fora da plataforma android (é mais simples). Usa o javafx para visualizar os outputs dos algoritmos e ajudar a perceber se a coisa está a funcionar conforme o pretendido.
* **arduino** : Código que corre no Arduino.

Na pasta [others-artifacts](https://github.com/jpmoreto/play-with-robots/tree/master/others-artifacts) encontra-se documentação usada na construção do robot como seja:
* O modelo 3d do mesmo usando [openscad](http://www.openscad.org/)
* O modelo da integração dos componentes electrónicos usando [fritzing](http://fritzing.org)
* Esquemáticos 2d do robot usando [qcad](https://qcad.org)

Descrição das principais classes/funções definidas em android/src/lib:
* jpm.lib.maps.**KDTreeD** : Esta classe implementa uma [KDTree](https://en.wikipedia.org/wiki/K-d_tree). Tem algumas decisões de implementação especificas do meu projecto, mas parece-me que é suficientemente genérica para poder ser usada por outros. Resumidamente, pretendo representar a ocupação num espaço 2d como um conjunto de quadrados (o tamanho de cada quadrado é passado no construtor). A arvore agrega espaços contiguos que tenham o mesmo estado de ocupação num único nó. Cada nó folha guarda o numero de vezes que foi marcado como ocupado ou como livre e a partir daí calcula uma probabilidade de ocupação. A probabilidade de ocupação é um intervalo fechado entre 0 e 1. Essa probabilidade representa 1 de 3 estados:
  * ocupação >= 0.5 + epsilon => espaço está ocupado com probabilidade correspondente a ocupação.
  * ocupação <= 0.5 - epsilon => espaço está livre com probabilidade correspondente a 1 - ocupação.
  * 0.5 - epsilon < ocupação < 0.5 + epsilon => desconheço o estado do espaço (não sei se está ocupado ou não)
  
  Esta classe permite verificar os pontos ocupados (ou livres) que interceptam um segmento de recta utilizando o método intersectRay.
  Permite tambem visitar todos os nós e por exemplo construir uma lista de rectangulos com os espaços livres.
  
* jpm.lib.math.**compactRectangles**: Esta função leva como argumento uma lista de rectangulos, e devolve como resultado uma lista de rectangulos em que agregou rectangulos adjacentes num único rectangulo. Se os rectangulos representarem por exemplo espaço livre (ou ocupado) a lista devolvida representa exatamente o mesmo espaço mas usando o menor numero de rectangulos que consiga.  
* jpm.lib.graph.graphbuilder.**GraphBuilder.build**: Esta função leva como argumento uma lista de rectangulos e devolve um grafo, criando um nó por cada rectangulo adjacente a outro, e ligações entre os vários nós sempre que é possivel ir de um para outro sem sair do rectangulo (TODO: explicar isto melhor).  
* jpm.lib.graph.algorithms.**AStarAlgorithm**: Esta classe implementa o algoritmo [a* search algorithm](https://en.wikipedia.org/wiki/A*_search_algorithm), que permite calcular o caminho mais curto entre 2 nós de um grafo.
* jpm.lib.graph.graphbuilder.**GraphBuilder.optimizePath** : Esta função leva como argumentos um caminho devolvido pelo AStarAlgorithm, e uma KDTreeD que representa a ocupação do espaço e devolve um caminho com menos nós, eliminando nós intermédios sempre que pode navegar directamente para um nó mais distante sem encontrar espaços ocupados (para verificar isso usa o método intersectRay da classe KDTreeD).

Neste momento o código existente em **lib** permite ir construindo a representação da ocupação de um espaço 2d usando a classe **KDTreeD**, derivar uma nova representação desse espaço ocupado "engordando-o" com a largura do robot, de maneira a poder considerá-lo como um ponto nos calculos restantes, e a partir daí construir um grafo que representa os caminhos possíveis entre quaisquer 2 pontos e calcular o caminho mais curto entre eles usando a classe **AStarAlgorithm**.
A classe **KDTreeDBuildGraphPath** existente debaixo da pasta **android/testsApp** tem um exemplo completo de como isso é feito e depois mostra visualmente usando javafx quer a representação do espaço ocupado quer a representação do caminho mais curto entre 2 pontos.
Os testes realizados num portatil Quad Core i5 2.50GHz (usando somente uma thread) com um espaço de 1024 x 1024 cm (+- 10 x 10 metros), ocupado aleatóriamente por 1500 pontos, cada um representando um quadrado de 2 x 2 cm, otem-se os seguintes resultados:
* tempo total de processamento do exemplo completo referido acima = +- 100 milli second
* numero total de rectangulos antes da compactação = +- 16000
* numero total de rectangulos depois da compactação = +- 2700
* tamanho do grafo gerado: +- 4800 nós e 33000 ligações entre eles
* tamanho do caminho inicialmente gerado com o algoritmo **AStarAlgorithm** = passa por +- 70 nós
* tamanho do caminho depois de optimizado = passa por +- 30 nós

+- 63% do tempo (100 ms) corresponde ao algoritmo de compactação dos rectangulos
Estes tempos podem ser reproduzidos utilizando a classe de testes **KDTreeDBuildGraphPath**


A classe **KDTreeD** implementa tambem um algoritmo que dado um segmento de recta e a representação de um espaço ocupado calcula o ponto de intersecção desse segmento de recta com o espaço ocupado mais proximo de um dos pontos do segmento. 
A classe **KDTreeDRayTracing** existente debaixo da pasta **android/testsApp** tem um exemplo completo de como isso é feito e depois mostra visualmente usando javafx quer a representação do espaço ocupado, todos os pontos de intersecção do segmento de recta com esse espaço, e o ponto de intersecção mais proximo de um dos pontos do segmento de recta.
