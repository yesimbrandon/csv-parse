import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Collections;
/**
 * FileParse
 */
public class FileParse {

    public static void main(String[] args) {
        parseFile("\\UploadFiles\\test1.csv");
    }

    public static void parseFile(String path) {
        String cwd = System.getProperty("user.dir");
        String csvFile = cwd + path;
        BufferedReader br = null;
        String line = "";
        List<String> insuranceCompanies = new ArrayList<String>();        
        Map<String, User> userList = new HashMap<>();
        List<String> badRecords = new ArrayList<String>();
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                validateRow(line, badRecords, userList, insuranceCompanies);
            }

            List<User> users = new ArrayList<User>();
            users.addAll(userList.values());      

            for (String company:insuranceCompanies) {                                          
                List<User> filteredUsers = users.stream().filter(x -> x.insuranceCompany.equals(company)).collect(Collectors.toList());                
                Collections.sort(filteredUsers);          
                
                FileWriter writer = new FileWriter(cwd + "\\OutputFiles\\" + company + ".csv");
                for(String row : filteredUsers.stream().map(User::getOriginal).collect(Collectors.toList())) {
                    writer.write(row + System.lineSeparator());
                }
                writer.close();
                      
            }

            if (!badRecords.isEmpty()) {
                FileWriter writer = new FileWriter(cwd + "\\OutputFiles\\bad_records.csv");
                for(String row : badRecords) {
                    writer.write(row + System.lineSeparator());
                }
                writer.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void validateRow(String line, List<String> badRecords, Map<String, User> userList, List<String> insuranceCompanies) {
        User user = new User();
        String[] splitVals = line.split(",");      
        if (splitVals.length != 5) {
            badRecords.add(line + ", Incorrect number of columns: " + splitVals.length);
            return;
        }          

        if (splitVals[0].isEmpty()) {
            badRecords.add(line + ", User ID is empty");
            return;
        }

        user.userId = splitVals[0];

        if (splitVals[1].isEmpty()) {
            badRecords.add(line + ", First Name is empty");
            return;
        }

        user.firstName = splitVals[1];

        if (splitVals[2].isEmpty()) {
            badRecords.add(line + ", Last Name is empty");
            return;
        }

        user.lastName = splitVals[2];
        
        if (!tryParseInt(splitVals[3], user)) {
            badRecords.add(line + ", Version not an int");
        }

        if (splitVals[4].isEmpty()) {
            badRecords.add(line + ", Insurance Company is empty");
            return;
        }

        user.insuranceCompany = splitVals[4];
        if (!insuranceCompanies.contains(user.insuranceCompany)) {
            insuranceCompanies.add(user.insuranceCompany);
        }
    
        user.originalRowValue = line;
        String userKey = user.insuranceCompany + user.userId;
        User existing = userList.get(userKey);
        if (existing != null && existing.version < user.version) {
            userList.replace(userKey, user);
        } else {
            userList.put(user.insuranceCompany + user.userId, user);
        }                
    }

    private static boolean tryParseInt(String value, User user) {  
        try {  
            user.version = Integer.parseInt(value);  
            return true;  
         } catch (NumberFormatException e) {  
            return false;  
         }  
   }
}