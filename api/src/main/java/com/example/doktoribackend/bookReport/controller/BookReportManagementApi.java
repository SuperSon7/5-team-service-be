package com.example.doktoribackend.bookReport.controller;

import com.example.doktoribackend.bookReport.dto.BookReportManagementResponse;
import com.example.doktoribackend.common.response.ApiResult;
import com.example.doktoribackend.common.swagger.AuthErrorResponses;
import com.example.doktoribackend.common.swagger.CommonErrorResponses;
import com.example.doktoribackend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "BookReport Management", description = "독후감 관리 API")
public interface BookReportManagementApi {

    @CommonErrorResponses
    @AuthErrorResponses
    @Operation(
            summary = "독후감 관리 조회",
            description = "모임장이 특정 회차의 독후감 제출 현황을 조회합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "code": "OK",
                              "message": "요청이 성공적으로 처리되었습니다.",
                              "data": {
                                "roundNo": 3,
                                "submittedCount": 4,
                                "totalCount": 6,
                                "members": [
                                  {
                                    "meetingMemberId": 10,
                                    "nickname": "startup",
                                    "bookReport": {
                                      "status": "APPROVED",
                                      "submittedAt": "2026-01-12T18:10:00+09:00"
                                    },
                                    "submissionRate": 70
                                  },
                                  {
                                    "meetingMemberId": 11,
                                    "nickname": "ella",
                                    "bookReport": {
                                      "status": "REJECTED",
                                      "submittedAt": "2026-01-12T19:00:00+09:00"
                                    },
                                    "submissionRate": 90
                                  },
                                  {
                                    "meetingMemberId": 12,
                                    "nickname": "john",
                                    "bookReport": null,
                                    "submissionRate": 50
                                  }
                                ]
                              }
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "code": "BOOK_REPORT_MANAGEMENT_FORBIDDEN",
                              "message": "독후감 관리 권한이 없습니다."
                            }
                            """)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "code": "ROUND_NOT_FOUND",
                              "message": "모임 회차를 찾을 수 없습니다."
                            }
                            """)
            )
    )
    ResponseEntity<ApiResult<BookReportManagementResponse>> getBookReportManagement(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "회차 ID", example = "123") Long roundId
    );
}
