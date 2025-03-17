package com.raulcg.auth.dtos;

import com.raulcg.auth.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessUserResponseDto {
    User user;
    String nameAttributeKey;
}
