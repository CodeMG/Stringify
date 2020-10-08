import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class FileStorer {


    public static void save(Map<Integer, ArrayList<Integer>> list){
        try{
            PrintWriter out = new PrintWriter("Savestate.txt");
            out.println("START");
            for(int i = 0; i < list.size();i++){
                int start = i;
                for(Integer end:list.get(i)){
                    out.println(start+":"+end);
                }
            }
            out.close();
        }catch(Exception e){

        }


    }

}
