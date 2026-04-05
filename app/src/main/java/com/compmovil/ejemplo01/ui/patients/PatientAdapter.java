package com.compmovil.ejemplo01.ui.patients;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;

import java.util.List;

import business.entities.PatientDTO;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder>{

    private List<PatientDTO> patientList;

    public PatientAdapter(List<PatientDTO> patientList) {
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el XML del item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_patient, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        PatientDTO patient = patientList.get(position);

        // nombre y apellido
        String fullName = patient.getName() + " " + patient.getLastName();
        holder.tvName.setText(fullName);

        //  DNI y Teléfono
        holder.tvDniPhone.setText("DNI " + patient.getDni() + " · " + patient.getPhone());

        // Avatar
        if (patient.getName() != null && !patient.getName().isEmpty()) {
            holder.tvAvatar.setText(String.valueOf(patient.getName().charAt(0)).toUpperCase());
        }

        // Listener para el botón de ver turnos del paciente
        holder.btnTurnos.setOnClickListener(v -> {
            // abrir un fragmento filtrando los turnos por el ID de este paciente
        });
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public void updateList(List<PatientDTO> newList) {
        patientList = newList;
        notifyDataSetChanged();
    }

    // El ViewHolder guarda las referencias a los TextViews
    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvDniPhone;
        ImageButton btnTurnos;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_nombre_paciente);
            tvDniPhone = itemView.findViewById(R.id.tv_dni_phone);
            btnTurnos = itemView.findViewById(R.id.btn_ver_turnos);
        }
    }
}
