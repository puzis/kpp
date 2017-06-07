package topology.graphParsers.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: danahend
 * Date: 23/09/12
 * Time: 10:48
 */

/**
 * Since there is special care for oregon extension and e.csv extension ,
 * all extensions split into three groups: 1. oregon files 2. e.csv files 3. all the rest
 * OREGON: since it the only extension at the middle of the file name and not at the suffix.
 * oregon file name structure should be one of the two:
 *  1. peer.oregon.[collection_date]
 *  2. peer.oregon+.[collection_date]
 *  (source:http://topology.eecs.umich.edu/data.html)
 *  CSV: since csv files end with e.csv
 */

public class FileRegularExpression {
    //public static Pattern FILE_NAME = Pattern.compile("peer.(oregon)\\+?\\.\\d+|.*\\.([_a-zA-Z0-9-]+)$");
    public static Pattern FILE_NAME = Pattern.compile("peer.(oregon)\\+?\\.\\d+|.*\\.(e.csv)|.*\\.([_a-zA-Z0-9-]+)$");

    public static String setExt (String filename_with_extension){
        Matcher m =  FileRegularExpression.FILE_NAME.matcher(filename_with_extension);
        m.find();
//        String ext = m.group(2);
//        if (ext==null) // if the extension is oregon  group 2 should be null
//            ext = m.group(1);
//        return ext;
        String group1 = m.group(1);
        String group2 = m.group(2);
        String group3 = m.group(3);
        if (group1!=null) return group1;
        else if (group2!=null) return  group2;
        else return group3;
    }
}

