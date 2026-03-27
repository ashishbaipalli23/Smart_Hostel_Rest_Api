package com.hostel.web.response;


import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timeStamp;

    private int status;

    private String errorMsg;

    private String path;

    private String method;

}