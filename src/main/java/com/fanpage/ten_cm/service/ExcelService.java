package com.fanpage.ten_cm.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.fanpage.ten_cm.entity.Donation; 

@Service
public class ExcelService {

    public void downloadDonationList(OutputStream out, List<Donation> donations) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("기부 내역 리스트");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"기부 번호", "기부자 ID", "기부 금액(원)", "기부 일시"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Donation donation : donations) {
                Row row = sheet.createRow(rowIdx++);
                
                row.createCell(0).setCellValue(donation.getId());
                
                if (donation.getUser() != null) {
                    row.createCell(1).setCellValue(String.valueOf(donation.getUser().getId())); 
                } else {
                    row.createCell(1).setCellValue("알 수 없음"); 
                }
                
                row.createCell(2).setCellValue(donation.getAmount());
                
                String timeStr = (donation.getCreatedAt() != null) ? donation.getCreatedAt().toString() : "";
                row.createCell(3).setCellValue(timeStr);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }
}