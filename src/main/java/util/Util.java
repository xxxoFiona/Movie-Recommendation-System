package util;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import Singleton.Movie;
import Singleton.Result;
import com.jasongoodwin.monads.Try;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;


import scala.Tuple2;

public  class Util {
    public static final String moviesPath = "C:\\ml-1m\\movies.dat";
    public static final String usersPath = "C:\\ml-1m\\users.dat";
    public static final String ratingsPath = "C:\\ml-1m\\ratings.dat";
    private static  MatrixFactorizationModel model=null;
    public static final String DOUBLECOLON = "::";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    private static final String SUBFIX = "part-00000";
//    private static Logger log = LoggerFactory.getLogger(Util.class);
    public static  JavaRDD<Movie> movieRDD;
    static JavaSparkContext jsc = new JavaSparkContext("local", "Recommendation Engine");
    static  Map<Integer ,String> map =new HashMap<>();
    static  Map<Integer ,String> map1 =new HashMap<>();




    public static String runSpark(int rank, int iterations, double labada) {


        JavaRDD<Rating> ratingRDD = jsc.textFile(ratingsPath).map(line -> {
            String[] ratingArr = line.split("::");
            Integer userId = Integer.parseInt(Try.ofFailable(() -> ratingArr[0]).orElse("-1"));
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> ratingArr[1]).orElse("-1"));
            Double rating = Double.parseDouble(Try.ofFailable(() -> ratingArr[2]).orElse("-1"));
            return new Rating(userId, movieId, rating);
        }).cache();


//        JavaRDD<Rating>[] ratingSplits = ratingRDD.randomSplit(new double[] { 0.8, 0.2 });

//        JavaRDD<Rating> ratingTrainingRDD = ratingSplits[0].cache();
//        JavaRDD<Rating> ratingTestRDD = ratingSplits[1].cache();
//
//        //System.out.println("Test Data Count ------- " + ratingTestRDD.count());

         model = ALS.train(JavaRDD.toRDD(ratingRDD), rank, iterations, labada);


        return "success";


    }
    public static List<Result> predict(int uid,int recNum) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {


        // 使用模型进行预测
        // 1. 找到uid没有评价过的movieIds

        JavaPairRDD<Integer, Integer> testUserProductRDD = movieRDD.mapToPair(movie->
                new Tuple2<>(uid, movie.getMovieID()));

        JavaRDD<Rating> predictionsForTestRDD = model.predict(testUserProductRDD);



        List<Result> arr=new ArrayList<>();
//        Set<Integer> candidates = Sets.difference((Set<Integer>) allMovieIds, userWithRatedMovies.get(uid));

        System.out.println("Test predictions");

        predictionsForTestRDD.take((int)predictionsForTestRDD.count()).stream().forEach(rating -> {
            System.out.println("Product id : " + rating.product() + "-- Rating : " + rating.rating()+map.get(rating.product()));
            arr.add(new Result(rating.product(),map.get(rating.product()),map1.get(rating.product()),rating.rating()));

        });
        Collections.sort(arr, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                Double first=o1.getRating();

                Double second=o2.getRating();
               return  second.compareTo(first);
            }
        });

        return arr.subList(0,recNum);



    }


    public static void init() throws IOException {


        System.out.println("start");
         movieRDD = jsc.textFile(moviesPath).map(line -> {
            final String[] movieArray = line.split("::");
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> movieArray[0]).orElse("-1"));
            map.put(movieId,movieArray[1]);
             map1.put(movieId,movieArray[2]);
            return new Movie(movieId, movieArray[1], movieArray[2]);
        }).cache();

        System.out.println("inti-finished");


    }









}

