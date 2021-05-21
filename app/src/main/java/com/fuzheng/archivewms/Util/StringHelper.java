package com.fuzheng.archivewms.Util;

import java.util.Arrays;
import java.util.List;

public class StringHelper {

    public static String StringJoin(String separator ,List<String> s) {
        StringBuilder csvBuilder = new StringBuilder();
        //拼接 除最后一个字符
        for (int i = 0; i < s.size() - 1; i++) {
            csvBuilder.append(s.get(i)).append(separator);
        }
        //拼接最后一个
        csvBuilder.append(s.get(s.size() - 1));
        System.out.println(csvBuilder.toString());
        return csvBuilder.toString();
    }
}
