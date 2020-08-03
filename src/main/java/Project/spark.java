package Project;
import Singleton.Movie;
import Singleton.Result;
import Singleton.User;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import com.jasongoodwin.monads.Try;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.*;
import org.apache.spark.sql.SQLContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class spark {
    public static final String moviesPath = "D:\\project\\movies.dat";
    public static final String usersPath = "D:\\project\\users.dat";
    public static final String ratingsPath = "D:\\project\\ratings.dat";
    public static void main(String[] args) {
        JavaSparkContext jsc = new JavaSparkContext("local", "Recommendation Engine");



        JavaRDD<Movie> movieRDD = jsc.textFile(moviesPath).map(line -> {
            final String[] movieArray = line.split("::");
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> movieArray[0]).orElse("-1"));
            return new Movie(movieId, movieArray[1], movieArray[2]);
        }).cache();

        JavaRDD<User> userRDD = jsc.textFile(usersPath).map(line -> {
            String[] userArr = line.split("::");
            Integer userId = Integer.parseInt(Try.ofFailable(() -> userArr[0]).orElse("-1"));
            Integer age = Integer.parseInt(Try.ofFailable(() -> userArr[2]).orElse("-1"));
            Integer occupation = Integer.parseInt(Try.ofFailable(() -> userArr[3]).orElse("-1"));
            return new User(userId, userArr[1], age, occupation, userArr[4]);
        }).cache();

        JavaRDD<Rating> ratingRDD = jsc.textFile(ratingsPath).map(line -> {
            String[] ratingArr = line.split("::");
            Integer userId = Integer.parseInt(Try.ofFailable(() -> ratingArr[0]).orElse("-1"));
            Integer movieId = Integer.parseInt(Try.ofFailable(() -> ratingArr[1]).orElse("-1"));
            Double rating = Double.parseDouble(Try.ofFailable(() -> ratingArr[2]).orElse("-1"));
            return new Rating(userId, movieId, rating);
        }).cache();

        JavaRDD<Rating>[] ratingSplits = ratingRDD.randomSplit(new double[] { 0.8, 0.2 });

        JavaRDD<Rating> ratingTrainingRDD = ratingSplits[0].cache();
        JavaRDD<Rating> ratingTestRDD = ratingSplits[1].cache();

        //System.out.println("Test Data Count ------- " + ratingTestRDD.count());

        MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(ratingTrainingRDD), 5, 10, 0.01);
//        model.save(jsc.sc(),"C:\\model");

//        JavaPairRDD<Integer, Integer> testUserProductRDD = ratingTestRDD.mapToPair(rating ->
//                new Tuple2<>(rating.user(), rating.product()));
//
//        JavaRDD<Rating> predictionsForTestRDD = model.predict(testUserProductRDD);

        //System.out.println("Predictions Count ------- " + predictionsForTestRDD.count());

        JavaPairRDD<Integer, Integer> testUserProductRDD = movieRDD.mapToPair(movie->
                new Tuple2<>(1, movie.getMovieID()));

        JavaRDD<Rating> predictionsForTestRDD = model.predict(testUserProductRDD);



        List<Result> arr=new ArrayList<>();
//        Set<Integer> candidates = Sets.difference((Set<Integer>) allMovieIds, userWithRatedMovies.get(uid));

        System.out.println("Test predictions");
        predictionsForTestRDD.take(10).stream().forEach(rating -> {
            System.out.println("Product id : " + rating.product() + "-- Rating : " + rating.rating());
//            arr.add(new Result(1,rating.product(),rating.rating()));

        });
        System.out.println(arr.get(0).getRating());



    }

}

