package org.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.model.Appointment;
import org.example.model.AppointmentStatus;
import org.example.model.User;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPaciente(User paciente);
    List<Appointment> findByPsicologa(User psicologa);
    List<Appointment> findByPsicologaAndHorarioBetweenAndStatus(
        User psicologa, LocalDateTime inicio, LocalDateTime fim, AppointmentStatus status);
    List<Appointment> findByHorarioBetweenAndStatus(
        LocalDateTime inicio, LocalDateTime fim, AppointmentStatus status);
}
