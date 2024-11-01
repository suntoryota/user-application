package com.example.demo.service.impl;

import com.example.demo.constant.BaseResponse;
import com.example.demo.domain.dto.UserReportDTO;
import com.example.demo.domain.entity.user.User;
import com.example.demo.domain.entity.user.UserRepository;
import com.example.demo.service.ReportService;
import com.example.demo.util.Helper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final Helper helper;

    public ReportServiceImpl(
            UserRepository userRepository,
            ResourceLoader resourceLoader,
            Helper helper) {
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.helper = helper;
    }

    @Override
    public BaseResponse<byte[]> generateUserReport() {
        try {
            // Load template
            Resource resource = resourceLoader.getResource("classpath:reports/user_report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());

            // Get data and convert to DTO
            List<User> users = userRepository.findAll();
            List<UserReportDTO> reportData = users.stream()
                    .map(user -> helper.getModelMapper().map(user, UserReportDTO.class))
                    .collect(Collectors.toList());

            // Set parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("reportTitle", "User Report");
            parameters.put("generatedDate", new Date());
            parameters.put("totalUsers", reportData.size());
            parameters.put("REPORT_LOCALE", new Locale("en", "US"));

            // Generate report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    parameters,
                    new JRBeanCollectionDataSource(reportData)
            );

            // Set margins and other properties
            jasperPrint.setLeftMargin(40);
            jasperPrint.setRightMargin(40);

            byte[] reportBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return BaseResponse.ok(reportBytes);

        } catch (Exception e) {
            log.error("Error generating PDF report: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error generating PDF report"
            );
        }
    }

    @Override
    public BaseResponse<byte[]> generateUserExcelReport() {
        try {
            List<User> users = userRepository.findAll();
            List<UserReportDTO> reportData = users.stream()
                    .map(user -> helper.getModelMapper().map(user, UserReportDTO.class))
                    .toList();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Users");

            // Style untuk header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style untuk data cells
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);

            // Create header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "First Name", "Last Name", "Email", "Phone Number", "Status"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data
            int rowNum = 1;
            for (UserReportDTO user : reportData) {
                Row row = sheet.createRow(rowNum++);

                Cell idCell = row.createCell(0);
                idCell.setCellValue(user.getId());
                idCell.setCellStyle(dataStyle);

                Cell firstNameCell = row.createCell(1);
                firstNameCell.setCellValue(user.getFirstName());
                firstNameCell.setCellStyle(dataStyle);

                Cell lastNameCell = row.createCell(2);
                lastNameCell.setCellValue(user.getLastName());
                lastNameCell.setCellStyle(dataStyle);

                Cell emailCell = row.createCell(3);
                emailCell.setCellValue(user.getEmail());
                emailCell.setCellStyle(dataStyle);

                Cell phoneCell = row.createCell(4);
                phoneCell.setCellValue(user.getPhoneNumber());
                phoneCell.setCellStyle(dataStyle);

                Cell statusCell = row.createCell(5);
                statusCell.setCellValue(user.getStatus().name());
                statusCell.setCellStyle(dataStyle);
            }

            // Auto size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                int currentWidth = sheet.getColumnWidth(i);
                if (currentWidth < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            // Convert to bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return BaseResponse.ok(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Error generating Excel report: ", e);
            return BaseResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Error generating Excel report"
            );
        }
    }
}
