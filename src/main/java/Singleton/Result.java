package Singleton;

public class Result {
    private Integer uid;
    private String name;
    private  String genre;
    private double   rating;

  public Result(int id, String name, String grene,double rating){
      this.uid=id;
      this.name=name;
      this.genre=grene;
      this.rating=rating;
  }

    public String getMovieid() {
        return name;
    }
    public String getGenre(){
      return genre;
    }

    public double getRating() {
        return rating;
    }

    public Integer getUid() {
        return uid;
    }
}
