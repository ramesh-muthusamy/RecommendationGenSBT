/**
  * Created by Ramesh Muthusamy on 11-02-2017.
  */


import org.apache.spark.{SparkConf, SparkContext}
import java.nio.file.{Paths, Files}
import java.util.logging.Logger

object RecommendationGenerator {
  //Matrix definition
  val matrix = Array.ofDim[String](20001, 11)
  //static list of attributes
  val attrList = Array("att-a", "att-b", "att-c", "att-d", "att-e", "att-f", "att-g", "att-h", "att-i", "att-j")
  //response map
  var responseMap: Map[String, Float] = Map()
  //response status
  var response = Array.fill[Float](matrix.length)(0)
  val conf = new SparkConf().
    setAppName("Recommendation generation on Spark and Scala").setMaster("local")
  val sc = new SparkContext(conf)
  val sqlContext = new org.apache.spark.sql.SQLContext(sc)
  //input location
  var inputLocation=""
  //outputLocation
  var outputLocation=""
  //key
  var articleKey=""



  def loadJSONToSparkStoreMatrix(location: String) {

    val df = sqlContext.read.format("json").load(location)
    val columnList = df.columns
    val row = df.head
    for (i <- 0 until columnList.length) {
      matrix(i + 1)(0) = columnList(i)
      var df_attr_list = row.getStruct(i)
      for (j <- 0 until attrList.length) {
        matrix(i + 1)(j + 1) = df_attr_list.getString(df_attr_list.fieldIndex(attrList(j)))
      }
    }
  }

  def filterContentOnKey(key: String, matrix: Array[Array[String]]): Array[Array[String]] = {
    val value = Array.ofDim[String](4, 1)
    value(0)(0) = key
    println(key)
    matrix.filter(x => {
      x(0) == key
    }).transpose
  }

  //matrix multiplication x X y
  def matrixMultiplierFunction(x: Array[Array[String]], y: Array[Array[String]]): Map[String, Float] = {

    for (i <- 0 until x.length; j <- 0 until x(0).length if i != 0 && j != 0; k <- 0 until y.length if k != 0 && j == k) {
      //println("equate"+y(k)(0)+":"+x(i)(j))
      if (y(k)(0).contentEquals(x(i)(j))) {
        //consider both upper and lower cases and use ascii values to provide weight for alphabets
        response(i) = response(i) + x(i)(j).toCharArray.map(_.toLower).filter(x => (x.toInt >= 97 && x.toInt <= 122)).map(x => (96 / x.toFloat)).reduce(_ + _)
      }
      if (j == x(0).length - 1 && !matrix(i)(0).contentEquals(y(0)(0))) {

        responseMap += (matrix(i)(0) -> response(i))
      }
    }
    responseMap
  }

  //determine the best matched recommendation
  def getRecommendation(key: String, matrix: Array[Array[String]]): Map[String, Float] = {
    matrixMultiplierFunction(matrix, filterContentOnKey(key, matrix))

  }
/*
Recommendation generator main
arg(0) - article id for which the recommendation is requested
arg(1) - input file location only local files allowed
arg(2) - output file location only local files allowed
 */
  def main(args: Array[String]) = {
    // the below variable only for local execution
  // System.setProperty("hadoop.home.dir", "C:\\winutil\\")
   println(System.getProperty("hadoop.home.dir"))
    val log = Logger.getLogger(RecommendationGenerator.getClass.getName)
    var isParamsOk = true
    if(args.length != 3) {
      isParamsOk=false
    }
    else{
      inputLocation = args(1)
      articleKey=args(0)
      outputLocation = args(2)
      if(Files.exists(Paths.get(outputLocation))) {
        log.severe("Output location exists already!")
        isParamsOk = false
      }
      if(!Files.exists(Paths.get(inputLocation))) {
        log.severe("Input location does not exist")
        isParamsOk = false
      }
    }
    if(!isParamsOk) {
      log.severe("Usage: RecommendationGenerator <articleKey> <input location> <output location>")
      sys.exit(1)
    }
    sc.setLogLevel("DEBUG")
    log.info("start")
    log.info("-------------------------------------------------------------------------")
    log.info("load in progress")
    loadJSONToSparkStoreMatrix(args(1))
    log.info("load completed")
    log.info("recommendation in progress")
    val responseRDD = sc.parallelize(getRecommendation(args(0), matrix).toSeq)
    log.info("recommendation completed")
    var outRDD=sc.parallelize(responseRDD.takeOrdered(11)(Ordering[Float].reverse.on(x => x._2)))
    outRDD.saveAsTextFile(outputLocation)
  }
}