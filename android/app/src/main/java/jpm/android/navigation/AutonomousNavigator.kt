package jpm.android.navigation

/**
 * Created by jm on 20/02/17.
 *
 */

/**
 * Created by jm on 11/02/17.
 *
 * http://pt.slideshare.net/GiladBarkan/bayesian-belief-networks-for-dummies
 * https://www.youtube.com/watch?v=TuGDMj43ehw
 *
 * utilizar bibliotecas em c c++:
 *   - https://kvurd.com/blog/compiling-a-cpp-library-for-android-with-android-studio/
 *   - https://developer.android.com/studio/projects/add-native-code.html?utm_campaign=android_discussion_cmake_110716&utm_source=anddev&utm_medium=blog
 *   - http://munteanumarian.blogspot.pt/2014/10/build-boost-for-android.html
 *   - http://official-rtab-map-forum.67519.x6.nabble.com/RTABMap-for-Android-td2528.html
 *
 *   https://bitbucket.org/gtborg/gtsam
 *
 * http://blog.davidsingleton.org/nnrccar/
 *
 * https://github.com/kanster/awesome-slam
 * https://github.com/liulinbo/slam
 * http://www.mrpt.org/List_of_SLAM_algorithms
 * https://openslam.org/
 * http://www.cctvcameraworld.com/robotics-resources-by-cctvcw.html
 * https://sourceforge.net/p/slam-plus-plus/wiki/Home/
 *
 * https://github.com/uzh-rpg/rpg_svo
 * http://www.robots-and-androids.com/SLAM-robot-navigation.html
 * https://www.doc.ic.ac.uk/~mly15/c2359z/computing_topics_website/introductiontoslam.html
 *
 * https://scholar.google.com/citations?user=A0ae1agAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=ATkNLcQAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=BtO5fTUAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=hczHVxEAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=MNp5hwoAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=SYYH37QAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=OEj04D0AAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=V5q8hW4AAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=et1GU2EAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=eZrWRbMAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=Cgp-L2UAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=TDSmCKgAAAAJ&hl=pt-BR&oi=sra
 * https://scholar.google.com/citations?user=yfXZfXEAAAAJ&hl=pt-BR&oi=sra
 *
 * http://www.pnas.org/content/106/21/8748.long
 * http://www.mdpi.com/1424-8220/12/7/9386/htm
 *
 * https://roboticsclub.org/
 *
 * https://en.wikipedia.org/wiki/List_of_mathematical_symbols
 * http://stattrek.com/statistics/notation.aspx
 * http://www.rapidtables.com/math/symbols/Statistical_Symbols.htm
 *
 * http://bilgin.esme.org/BitsAndBytes/KalmanFilterforDummies
 * https://home.wlu.edu/~levys/kalman_tutorial/
 *
 *
 * Estudar melhor:
 *   - error covariance matrix
 *   - Bayes networks
 *   - Markov chain
 *   - kalman filter
 *   - Factor graphs
 *   - particles Particle Filter SLAM based methods or sequential Monte-Carlo (SMC) method
 *
 * SLAM algoritms:
 *   - GTSAM
 *   - FastSLAM 2.0
 *   - SLAM++
 *   - CESLAM
 *   - ROSLAM
 *   - EKF-SLAM
 *   -
 *   - opengm
 *
 * Questões:
 *   - representação do mapa:
 *      - matriz de ocupação?
 *      - vectorial considerando só segmentos de recta? considerando que são paralelos ou perpendiculares entre eles?
 *   - com o robot em movimento as medições do sonar correspondem a tempos diferentes, logo tambem a "poses" diferentes do robot, como endereçar esta questão?
 *   - que algoritmos slam utilizar? filtros extendidos de kafman? particulas?
 *   - como contemplar o dinamismo do espaço envolvente e exclui-lo da construção do mapa mas inclui-lo ao evitar obstáculos?
 *
 *   - caracteristicas físicas do robot:
 *      - tendo os sensores de rotação nas rodas com os motores este é sujeito a erros relacionados com a derrapagem das mesmas,
 *        mas se tivesse um sensor numa roda sem carga (motor) este problema seria minimizado.
 *
 *  http://www.ikaros-project.org/articles/2008/gridmaps/
 *  http://jimkang.com/quadtreevis/
 *  https://en.wikipedia.org/wiki/Quadtree
 *  http://blog.ivank.net/quadtree-visualization.html
 *  http://zufallsgenerator.github.io/2014/01/26/visually-comparing-algorithms/
 *  https://www.hindawi.com/journals/ijcgt/2015/736138/
 *
 * http://www.mrpt.org/List_of_SLAM_algorithms
 * http://www.mrpt.org/
 *
 * http://robotics.stackexchange.com/questions/2324/kinematics-of-a-4-wheeled-differential-drive-robots
 * http://devmag.org.za/2011/02/23/quadtrees-implementation/
 * https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
 *
 * http://robotics.stackexchange.com/questions/952/whats-an-efficient-way-to-visit-every-reachable-space-on-a-grid-with-unknown-ob
 * http://www.personal.kent.edu/~rmuhamma/Compgeometry/MyCG/PolyPart/polyPartition.htm
 *
 * https://github.com/psigen/robotutils
 * http://robotsforroboticists.com/kalman-filtering/
 * https://www.codeproject.com/articles/469458/kalman-filtering-part-2
 * https://www.codeproject.com/articles/326657/kalmandemo
 * http://ejml.org/wiki/index.php?title=Example_Kalman_Filter#SimpleMatrix_Example
 * https://sourceforge.net/directory/os:linux/?q=extended%20kalman%20filter%20java
 * https://sourceforge.net/projects/jkalman/?source=typ_redirect
 * https://github.com/habsoft/robosim  https://sourceforge.net/projects/r-localization/?source=directory
 *
 * http://ardupilot.org/dev/docs/extended-kalman-filter.html
 * http://traffic.berkeley.edu/project/downloads
 * https://github.com/villoren/KalmanLocationManager
 * https://github.com/Bresiu/KalmanFilter
 * https://github.com/kibotu/KalmanRx ****
 * https://github.com/ThomasDavine/kalman-filter
 * https://github.com/lessthanoptimal/ejml ****
 *
 * https://onlinecourses.science.psu.edu/stat200/node/36 ******* estatistica
 */

class AutonomousNavigator