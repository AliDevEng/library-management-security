package com.example.library_management_v2.service;

import com.example.library_management_v2.dto.CreateUserDTO;
import com.example.library_management_v2.dto.UserDTO;
import com.example.library_management_v2.entity.User;
import com.example.library_management_v2.exception.DuplicateUserException;
import com.example.library_management_v2.exception.UserNotFoundException;
import com.example.library_management_v2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserDTO getUserByEmail (String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Användare med email: " + email + " hittades inte!") );

        return convertToDTO(user);
    }


    /**
     * Konverterar en User entity till UserDTO (utan lösenord)
     * user User entity att konvertera
     * returnera Konverterad UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRegistrationDate(user.getRegistrationDate());
        // Lösenordet inkluderas inte i DTO:n av säkerhetsskäl
        return dto;
    }


    // Skapa en ny användare
    public UserDTO createUser(CreateUserDTO createUserDTO) {

        // Kontroll om användaren redan finns
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new DuplicateUserException
                    ("En användare med e-postadressen " + createUserDTO.getEmail() + " finns redan");
        }

        // Skapa ny användarentitet
        User user = new User();
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setEmail(createUserDTO.getEmail());

        // KRITISKT: Kryptera lösenordet innan sparande
        // Lösenordet går från klartext till BCrypt hash här
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        // Sätt registreringsdatum till dagens datum
        user.setRegistrationDate(LocalDate.now());

        // Nya användare ska få automatisk USER-roll
        user.setRole("USER");

        // Aktivera användaren direkt efter registrering
        user.setEnabled(true);

        // Spara användaren med krypterat lösenord
        User savedUser = userRepository.save(user);

        // Returnera den skapade användaren som DTO (utan lösenord)
        return convertToDTO(savedUser);
    }
}
