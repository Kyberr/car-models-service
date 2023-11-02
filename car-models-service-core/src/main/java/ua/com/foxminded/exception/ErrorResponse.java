package ua.com.foxminded.exception;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<Violation> violations;
}
