package jsonPojo.Query;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;

import java.io.StringReader;

public class QueryFormatter {

    public static boolean isValid(final String query) {
        try {
            CCJSqlParserManager parser =  new CCJSqlParserManager();
            parser.parse(new StringReader(query));
            return true;
        } catch (JSQLParserException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String format(String query) {
        StringBuilder sb = new StringBuilder();
        char bracket = '(';
        for ( int index =0 ; index < query.length() ; index++) {
            char character = query.charAt(index);
            if(character == bracket) {

            } else {
                sb.append(character);
            }
        }
        return "";
    }
}
