package apps.nerdyginger.pocketpantry;


import java.lang.reflect.Type;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class Converters {
    /*
       @TypeConverter
    public static ArrayList<String> fromString(String value) {
        List<String> split = Arrays.asList(value.split("\\s*,\\s*"));
        return (ArrayList<String>) split;
    }
     */

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
