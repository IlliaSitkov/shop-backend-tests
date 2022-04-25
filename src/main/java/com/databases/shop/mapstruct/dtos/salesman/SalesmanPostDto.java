package com.databases.shop.mapstruct.dtos.salesman;

import com.databases.shop.models.PersonName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
public class SalesmanPostDto {

    @JsonProperty("full_name")
    @NotNull
    private PersonName personName;

    @JsonProperty("tel_nums")
    @NotNull
    private Set<TelephoneDto> telephones;

    @JsonProperty("email")
    @NotNull
    private String email;

    @JsonProperty("dateOfBirth")
    @NotNull
    private Date dateOfBirth;

    @JsonProperty("dateOfHiring")
    @NotNull
    private Date dateOfHiring;

    @JsonProperty("password")
    @NotNull
    private String password;

    @JsonProperty("role")
    @NotNull
    private String role;
}
