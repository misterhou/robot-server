package com.fanyumeta.client;

import lombok.Data;

@Data
public class LargeModelResponse {
    private String id;
    private String object;
    private Long crated;
    private String result;
    private Usage usage;

    @Data
    class Usage {
        private String prompt_tokens;
        private String completion_tokens;
        private String total_tokens;
    }
}
