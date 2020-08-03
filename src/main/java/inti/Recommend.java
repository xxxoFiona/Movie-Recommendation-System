package inti;
import Singleton.Result;
import util.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Servlet implementation class Recommend
 */
@WebServlet("/Recommend")
public class Recommend extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Recommend() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = request.getParameter("userId");
        String flag = request.getParameter("flag");// flag : recommend/check
        String recNum =null;

        List<Result> rec =null;
            recNum = request.getParameter("recommendNum");
            try {
                rec= Util.predict(Integer.parseInt(uid),Integer.parseInt(recNum));
            } catch (NumberFormatException | IllegalAccessException | InstantiationException | InvocationTargetException
                    | NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        StringBuffer buffer = new StringBuffer();
        if("check".equals(flag)){
            buffer.append(rec==null?"0":rec.size()).append("::");
        }
        for(Result re:rec){
            buffer.append("<tr>")
                    .append("<td>").append(re.getUid()).append("</td>")
                    .append("<td>").append(re.getMovieid()).append("</td>")
                    .append("<td>").append(re.getGenre()).append("</td>")
                    .append("<td>").append(re.getRating()).append("</td>")
                    .append("</tr>");
        }

        // 打印输出
        PrintWriter out = response.getWriter();
        if(buffer.length()<=0){
            out.write("");
        }else{
            out.write(buffer.toString());
        }
        out.flush();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}

