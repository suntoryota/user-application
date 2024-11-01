package com.example.demo.service;

import com.example.demo.constant.BaseResponse;

public interface ReportService {
    BaseResponse<byte[]> generateUserReport();
    BaseResponse<byte[]> generateUserExcelReport();
}
