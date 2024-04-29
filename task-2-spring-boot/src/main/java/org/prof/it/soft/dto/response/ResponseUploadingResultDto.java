package org.prof.it.soft.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "success_uploaded",
        "failed_uploaded"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ResponseUploadingResultDto {

    /**
     * The number of successfully uploaded files.
     */
    @JsonProperty("success_uploaded")
    private Integer successUploaded;

    /**
     * The number of failed uploaded files.
     */
    @JsonProperty("failed_uploaded")
    private Integer failedUploaded;

    /**
     * The message.
     */
    @JsonProperty("message")
    private String message;
}
