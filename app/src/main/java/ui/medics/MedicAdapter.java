package ui.medics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;

import java.util.List;

import business.entities.MedicDTO;

public class MedicAdapter extends RecyclerView.Adapter<MedicAdapter.MedicViewHolder>{

    private List<MedicDTO> medicList;

    public MedicAdapter(List<MedicDTO> medicList) {
        this.medicList = medicList;
    }

    @NonNull
    @Override
    public MedicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el XML del item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medic, parent, false);
        return new MedicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicViewHolder holder, int position) {
        MedicDTO medic = medicList.get(position);

        // name and lastname
        String fullName = medic.getName() + " " + medic.getLastName();
        holder.tvName.setText(fullName);

        //  registration and speciality
        holder.tvSpecMat.setText(medic.getSpeciality() + " · Mat: " + medic.getRegistration());

        // Avatar
        if (medic.getName() != null && !medic.getName().isEmpty()) {
            holder.tvAvatar.setText(String.valueOf(medic.getName().charAt(0)).toUpperCase());
        }

        // Listener para el botón de ver turnos del paciente
        holder.btnTurnos.setOnClickListener(v -> {
            // abrir un fragmento filtrando los turnos por el ID de este paciente
        });
    }

    @Override
    public int getItemCount() {
        return medicList.size();
    }

    public void updateList(List<MedicDTO> newList) {
        medicList = newList;
        notifyDataSetChanged();
    }

    // El ViewHolder guarda las referencias a los TextViews
    static class MedicViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvName, tvSpecMat;
        ImageButton btnTurnos;

        public MedicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar_medico);
            tvName = itemView.findViewById(R.id.tv_nombre_medico);
            tvSpecMat = itemView.findViewById(R.id.tv_especialidad_matricula);
            btnTurnos = itemView.findViewById(R.id.btn_ver_turnos_medico);
        }
    }
}
