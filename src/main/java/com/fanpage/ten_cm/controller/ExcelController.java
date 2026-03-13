package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.Donation;
import com.fanpage.ten_cm.repository.DonationRepository; // 기부 내역 DB를 가져올 Repository
import com.fanpage.ten_cm.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
// import javax.servlet.http.HttpServletResponse; // 스프링 버전에 따라 jakarta 대신 javax일 수 있습니다.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private DonationRepository donationRepository;

    // HTML에서 엑셀 다운로드 버튼이 가리키는 주소!
    @GetMapping("/excel/download") 
    public void downloadExcel(HttpServletResponse response) throws IOException {
        
        // 1. 브라우저에게 "이건 화면이 아니라 다운로드할 엑셀 파일이야!" 라고 알려주기
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        // 다운로드될 파일 이름 설정
        response.setHeader("Content-Disposition", "attachment; filename=\"donation_list.xlsx\"");

        // 2. DB에서 모든 기부 내역 싹 다 가져오기
        List<Donation> donations = donationRepository.findAll();

        // 3. 아까 만든 ExcelService에게 엑셀 파일 만들어서 브라우저로 쏴달라고 부탁하기
        excelService.downloadDonationList(response.getOutputStream(), donations);
    }
}