package ui.mis_turnos;

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
        this.patientDAO = new PatientDAO(context);
        this.medicDAO = new MedicDAO(context);
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_turno, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentDTO appointment = appointmentList.get(position);

        holder.tvDate.setText(appointment.getDate());
        holder.tvTime.setText(appointment.getTime());

        PatientDTO patient = patientDAO.getById(appointment.getIdPatient());
        if (patient != null) {
            holder.tvName.setText(patient.getName() + " " + patient.getLastName());
        } else {
            holder.tvName.setText("Paciente no encontrado");
        }

        MedicDTO medic = medicDAO.getById(appointment.getIdMedic());
        if (medic != null) {
            holder.tvMedic.setText("Dr/a. " + medic.getName() + " " + medic.getLastName());
        } else {
            holder.tvMedic.setText("Médico no encontrado");
        }

        // Set the state text into the state chip
        if (holder.chipStatus != null) {
            holder.chipStatus.setText(appointment.getState() != null ? appointment.getState().toString() : "PENDING");
        }

        try {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaTurno = LocalDate.parse(appointment.getDate());

            if (fechaTurno.isEqual(hoy)) {
                if (holder.chipToday != null) holder.chipToday.setVisibility(View.VISIBLE);
                if (holder.chipPassed != null) holder.chipPassed.setVisibility(View.GONE);
            } else if (fechaTurno.isBefore(hoy)) {
                if (holder.chipToday != null) holder.chipToday.setVisibility(View.GONE);
                if (holder.chipPassed != null) holder.chipPassed.setVisibility(View.VISIBLE);
            } else {
                if (holder.chipToday != null) holder.chipToday.setVisibility(View.GONE);
                if (holder.chipPassed != null) holder.chipPassed.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (holder.chipToday != null) holder.chipToday.setVisibility(View.GONE);
            if (holder.chipPassed != null) holder.chipPassed.setVisibility(View.GONE);
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

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvName, tvMedic, tvTime;
        Chip chipStatus, chipToday, chipPassed; // renamed chipPending to chipStatus

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_fecha);
            tvName = itemView.findViewById(R.id.tv_paciente);
            tvMedic = itemView.findViewById(R.id.tv_medico);
            tvTime = itemView.findViewById(R.id.tv_hora);

            // FIX: Using correct IDs from item_turno.xml
            chipStatus = itemView.findViewById(R.id.chip_estado);
            chipPassed = itemView.findViewById(R.id.chip_vencido);
            chipToday = itemView.findViewById(R.id.chip_hoy);
        }
    }
}
