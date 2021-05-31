import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class testScript {
    public static void main(String[] args) throws UnirestException, IOException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("https://api.github.com/repos/orgs/resps/issues?state=all")
                .header("Authorization", "Bearer")
                .asString();
        String body = response.getBody();
        FileOutputStream fos = null;
        XSSFWorkbook wb = null;
        //project https://api.github.com/repos/DaChanBay/System-Source-Code/projects

        try{
            fos = new FileOutputStream("./GithubIssueExport.xlsx");
            wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("Issue");
            XSSFRow titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("Issue Number");
            titleRow.createCell(1).setCellValue("Title");
            titleRow.createCell(2).setCellValue("State");
            titleRow.createCell(3).setCellValue("Labels");
            titleRow.createCell(4).setCellValue("Created By");
            titleRow.createCell(5).setCellValue("Created");
            titleRow.createCell(6).setCellValue("Closed By");
            titleRow.createCell(7).setCellValue("Closed at");

            ObjectMapper objectMapper = new ObjectMapper();
            List<JsonNode> nodes = objectMapper.readValue(body, new TypeReference<List <JsonNode>>(){});
            for(JsonNode node: nodes){
                XSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                String number = node.get("number").asText();
                String title = node.get("title").asText();
                String state = node.get("state").asText();
                row.createCell(0).setCellValue(number);
                row.createCell(1).setCellValue(title);
                row.createCell(2).setCellValue(state);
                JsonNode labels = node.get("labels");
                String label = "";
                for(int i=0; i < labels.size();i++){
                    label += labels.get(i).get("name") + ",";
                }
                row.createCell(3).setCellValue(label);
            }

            wb.write(fos);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            wb.close();
            fos.close();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> nodes = objectMapper.readValue(body, new TypeReference<List <JsonNode>>(){});
        for(JsonNode node: nodes){
            String title = node.get("title").asText();
            System.out.println(title);
        }
    }
}
