package org.example.repository;

import org.example.model.Agendamento;
import org.example.model.AgendamentoStatus;
import org.example.model.Clinica;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
// Changed ID type from UUID to Long
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // --- Find methods using Entities (ensure related entities have Long IDs) ---
    List<Agendamento> findByClinicaAndPaciente(Clinica clinica, User paciente);
    List<Agendamento> findByClinicaAndProfissional(Clinica clinica, User profissional);

    // --- Find methods using Long IDs --- 
    List<Agendamento> findByPacienteId(Long pacienteId);
    List<Agendamento> findByProfissionalId(Long profissionalId);

    // Find by professional, date range, and status (essential for schedule view)
    // Note: Passing full entities might be slightly less performant than IDs but can be clearer
    List<Agendamento> findByClinicaAndProfissionalAndDataHoraInicioBetweenAndStatusIn(
            Clinica clinica, User profissional, OffsetDateTime inicio, OffsetDateTime fim, List<AgendamentoStatus> status);
            
    // Alternative using IDs (potentially more performant if entities are not already loaded)
    List<Agendamento> findByProfissionalIdAndDataHoraInicioBetweenAndStatusInOrderByDataHoraInicioAsc(
            Long profissionalId, OffsetDateTime inicio, OffsetDateTime fim, List<AgendamentoStatus> status);

    // Find by clinic, date range, and status (clinic overview)
    List<Agendamento> findByClinicaAndDataHoraInicioBetweenAndStatusIn(
            Clinica clinica, OffsetDateTime inicio, OffsetDateTime fim, List<AgendamentoStatus> status);

    // Find by patient and date range
     List<Agendamento> findByPacienteIdAndDataHoraInicioBetweenOrderByDataHoraInicioAsc(
            Long pacienteId, OffsetDateTime inicio, OffsetDateTime fim);

    // Find future appointments for a specific patient
    List<Agendamento> findByPacienteIdAndDataHoraInicioAfterOrderByDataHoraInicioAsc(
            Long pacienteId, OffsetDateTime agora);

    // Find future appointments for a specific professional
    List<Agendamento> findByProfissionalIdAndDataHoraInicioAfterOrderByDataHoraInicioAsc(
            Long profissionalId, OffsetDateTime agora);

    // --- Custom Queries using Long IDs --- 

    // Query to check overlapping appointments for a specific professional
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.profissional.id = :profissionalId " +
           "AND a.status NOT IN (org.example.model.AgendamentoStatus.CANCELADO_PACIENTE, org.example.model.AgendamentoStatus.CANCELADO_CLINICA, org.example.model.AgendamentoStatus.NAO_COMPARECEU) " +
           "AND ((a.dataHoraInicio < :fim AND a.dataHoraFim > :inicio))" ) // Checks for overlap
    boolean existsOverlappingAppointmentForProfessional(
            @Param("profissionalId") Long profissionalId, // Changed to Long
            @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim);

     // Query to check overlapping appointments for a specific patient
    @Query("SELECT COUNT(a) > 0 FROM Agendamento a WHERE a.paciente.id = :pacienteId " +
           "AND a.status NOT IN (org.example.model.AgendamentoStatus.CANCELADO_PACIENTE, org.example.model.AgendamentoStatus.CANCELADO_CLINICA, org.example.model.AgendamentoStatus.NAO_COMPARECEU) " +
           "AND ((a.dataHoraInicio < :fim AND a.dataHoraFim > :inicio))" )
    boolean existsOverlappingAppointmentForPaciente(
            @Param("pacienteId") Long pacienteId, // Changed to Long
            @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim);

    // Add other queries as needed, e.g., for finding by recurrence parent ID (Long)
    // List<Agendamento> findByRecorrenciaPaiId(Long recorrenciaIdPai);
}

