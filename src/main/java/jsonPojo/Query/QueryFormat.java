package jsonPojo.Query;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFormat {

    private String inputString;


    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    public String queryFromatting(String inputString){

        String str1 = inputString.replace("DC.", ""); // replacing point 1 .. DC.
        String[] tmp = str1.split("FROM"); //for this I will show.. this bit is tricky..for me ;)
        //change this theme to white pls okies

        String part1 = tmp[1];
        System.out.println(part1);
        part1 = part1.replace("AND   (  ( @Prompt('Select raw data time resolution:','A',{'Hour','Raw data'},mono,constrained)=@Prompt('Select raw data time resolution:','A',{'Hour','Raw data'},mono,constrained},mono,constrained) )", "");
        System.out.println(part1);
        String firstString = "AND  ( ( DIM_DATE.DATE_ID ) BETWEEN @Prompt";
        String endString = "optional)  )";
        part1 = part1.replaceAll("(&firstString=)[^&]*(&endString=)", "");
        System.out.println(part1);

        String part2 = tmp[0];

        //System.out.println(part2);

        ArrayList<String> ar = new ArrayList<String>();
        String pattern1 = ".";
        String pattern2 = ",";
        String regexString = Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2);

        Pattern pattern = Pattern.compile(regexString);
// text contains the full text that you want to extract data
        Matcher matcher = pattern.matcher(part2);
        System.out.println("part2  " + part2);

        while (matcher.find()) {
            ar.add(matcher.group(1));
            //System.out.println(matcher.group(1));
        }
        System.out.println(ar.toString());
         return part1;

    }
}
