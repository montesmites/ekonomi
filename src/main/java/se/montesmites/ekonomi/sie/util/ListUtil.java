package se.montesmites.ekonomi.sie.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

  public static <T> List<T> append(List<T> list, T element) {
    var res = new ArrayList<>(list);
    res.add(element);
    return res;
  }

  public static <T> List<T> concat(List<T> list1, List<T> list2) {
    var concat = new ArrayList<>(list1);
    concat.addAll(list2);
    return List.copyOf(concat);
  }
}
