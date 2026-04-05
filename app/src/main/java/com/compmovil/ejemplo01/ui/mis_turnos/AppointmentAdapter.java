package com.compmovil.ejemplo01.ui.mis_turnos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;
import com.google.android.material.chip.Chip;

import java.time.LocalDate;
import java.util.List;

import business.entities.AppointmentDTO;
import business.entities.MedicDTO;
import business.entities.PatientDTO;
import business.persistence.MedicDAO;
import business.persistence.PatientDAO;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>{

    private List<AppointmentDTO> appointmentList;

    private PatientDAO patientDAO;
    private MedicDAO medicDAO;

    public AppointmentAdapter(Context context, List<AppointmentDTO> appointmentList) {
        this.appointmentList = appointmentList;
        // Inicializamos los DAOs aquí para no tener que crearlos en cada fila
        this.patientDAO = new PatientDAO(context);
        this.medicDAO = new MedicDAO(context);
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el XML del item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_turno, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentDTO appointment = appointmentList.get(position);

        // Setear Fecha y Hora
        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(appointment.getTime());

        // Paciente ID y setear el nombre
        PatientDTO patient = patientDAO.getById(appointment.getIdPatient());
        if (patient != null) {
            holder.tvName.setText(patient.getName() + " " + patient.getLastName());
        } else {
            holder.tvName.setText("Paciente no encontrado");
        }

        // Medic ID y setear el nombre
        MedicDTO medic = medicDAO.getById(appointment.getIdMedic());
        if (medic != null) {
            holder.tvMedic.setText("Dr/a. " + medic.getName() + " " + medic.getLastName());
        } else {
            holder.tvMedic.setText("Médico no encontrado");
        }

        try {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaTurno = LocalDate.parse(appointment.getDate()); // espera "YYYY-MM-DD"

            if (fechaTurno.isEqual(hoy)) {
                holder.chipToday.setVisibility(View.VISIBLE);
                holder.chipPassed.setVisibility(View.GONE);
                holder.chipPending.setVisibility(View.GONE);
            } else if (fechaTurno.isBefore(hoy)) {
                holder.chipPassed.setVisibility(View.VISIBLE);
                holder.chipToday.setVisibility(View.GONE);
                holder.chipPending.setVisibility(View.GONE);
            } else {
                holder.chipPending.setVisibility(View.VISIBLE);
                holder.chipToday.setVisibility(View.GONE);
                holder.chipPassed.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // si hay una fecha mal guardada, ocultamos los chips para no crashear
            e.printStackTrace();
            holder.chipToday.setVisibility(View.GONE);
            holder.chipPassed.setVisibility(View.GONE);
            holder.chipPending.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public void updateList(List<AppointmentDTO> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }

    // El ViewHolder guarda las referencias a los TextViews y Chips
    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvName, tvMedic, tvTime;
        Chip chipPending, chipToday, chipPassed;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_fecha);
            tvName = itemView.findViewById(R.id.tv_paciente);
            tvMedic = itemView.findViewById(R.id.tv_medico);
            tvTime = itemView.findViewById(R.id.tv_hora);

            chipPending = itemView.findViewById(R.id.chip_pendientes);
            chipPassed = itemView.findViewById(R.id.chip_vencido);
            chipToday = itemView.findViewById(R.id.chip_hoy);
        }
    }
}