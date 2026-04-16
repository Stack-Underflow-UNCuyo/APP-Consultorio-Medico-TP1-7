package ui.mis_turnos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.compmovil.ejemplo01.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import business.entities.AppointmentDTO;
import business.entities.MedicDTO;
import business.entities.PatientDTO;
import business.entities.StateAppointment;
import business.persistence.AppointmentDAO;
import business.persistence.MedicDAO;
import business.persistence.PatientDAO;

public class TurnosFragment extends Fragment {

    private RecyclerView rvAppointments;
    private LinearLayout layoutEmpty;
    private TextView tvCountPending;
    private TextView tvCountToday;
    private FloatingActionButton fab;

    private AppointmentDAO appointmentDAO;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentDTO> appointmentList;
    private List<AppointmentDTO> todayAppointments;
    private List<AppointmentDTO> pendingAppointments;
    private ChipGroup chipGroupFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_turnos, container, false);

        rvAppointments = view.findViewById(R.id.rv_turnos);
        layoutEmpty = view.findViewById(R.id.layout_empty_turnos);
        tvCountPending = view.findViewById(R.id.tv_count_pendientes);
        tvCountToday = view.findViewById(R.id.tv_count_hoy);
        fab = view.findViewById(R.id.fab_nuevo_turno);
        chipGroupFilter = view.findViewById(R.id.chipgroup_filter);

        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentDAO = new AppointmentDAO(getContext());

        loadAppointments();

        fab.setOnClickListener(v -> showDialogNewAppointment());

        chipGroupFilter.setOnCheckedStateChangeListener((chipGroup, list) -> {
            if (!list.isEmpty()) {
                for (Integer id : list){
                    if (id == R.id.chip_pendientes){
                        updateViewList(pendingAppointments);
                    } else if(id == R.id.chip_todos){
                        updateViewList(appointmentList);
                    } else if (id == R.id.chip_group_hoy) {
                        updateViewList(todayAppointments);
                    }
                }
            }
        });

        return view;
    }

    private void updateViewList(List<AppointmentDTO> list){
        if (appointmentAdapter == null) {
            appointmentAdapter = new AppointmentAdapter(getContext(), list);
            rvAppointments.setAdapter(appointmentAdapter);
        } else {
            appointmentAdapter.updateList(list);
        }

        if (list == null || list.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.VISIBLE);
        }
    }

    private void loadAppointments() {
        appointmentList = appointmentDAO.getAll();
        pendingAppointments = new ArrayList<>();
        todayAppointments = new ArrayList<>();

        LocalDate today = LocalDate.now();
        int countToday = 0;
        int countPending = 0;

        for (AppointmentDTO appointment : appointmentList) {
            try {
                LocalDate apptDate = LocalDate.parse(appointment.getDate());

                if (apptDate.isEqual(today)) {
                    countToday++;
                    todayAppointments.add(appointment);
                    pendingAppointments.add(appointment);
                } else if (apptDate.isAfter(today)) {
                    countPending++;
                    pendingAppointments.add(appointment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tvCountToday.setText(String.valueOf(countToday));
        tvCountPending.setText(String.valueOf(countPending));
        
        // Refresh with "All" by default
        updateViewList(appointmentList);
    }

    private void showDialogNewAppointment() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_appointment, null);

        TextInputLayout tilPatient = dialogView.findViewById(R.id.til_paciente);
        TextInputLayout tilMedic = dialogView.findViewById(R.id.til_medico);
        AutoCompleteTextView actvPatient = dialogView.findViewById(R.id.actv_paciente);
        AutoCompleteTextView actvMedic = dialogView.findViewById(R.id.actv_medico);
        CalendarView calendarView = dialogView.findViewById(R.id.calendar_view);
        ChipGroup chipGroupTimes = dialogView.findViewById(R.id.chipgroup_horarios);
        TextView tvSelectedDate  = dialogView.findViewById(R.id.tv_fecha_seleccionada);
        TextView tvSelectedTime   = dialogView.findViewById(R.id.tv_hora_seleccionada);

        // Final arrays to be modified inside lambdas
        final long[] selectedIdPatient = { -1L };
        final long[] selectedIdMedic = { -1L };
        final String[] selectedDate = { "" };
        final String[] selectedTime = { "" };

        // Patient List
        PatientDAO patientDAO = new PatientDAO(requireContext());
        List<PatientDTO> patients = patientDAO.getAll();
        String[] patientNames = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            patientNames[i] = patients.get(i).getName() + " " + patients.get(i).getLastName();
        }

        ArrayAdapter<String> adapterPatients = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, patientNames);
        actvPatient.setAdapter(adapterPatients);
        actvPatient.setOnItemClickListener((parent, v, position, id) -> {
            selectedIdPatient[0] = patients.get(position).getId();
            tilPatient.setError(null);
        });

        // Medic List
        MedicDAO medicDAO = new MedicDAO(requireContext());
        List<MedicDTO> medics = medicDAO.getAll();
        String[] medicsNames = new String[medics.size()];
        for (int i = 0; i < medics.size(); i++) {
            medicsNames[i] = "Dr. " + medics.get(i).getName() + " " + medics.get(i).getLastName();
        }

        ArrayAdapter<String> adapterMedicos = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, medicsNames);
        actvMedic.setAdapter(adapterMedicos);
        actvMedic.setOnItemClickListener((parent, v, position, id) -> {
            selectedIdMedic[0] = medics.get(position).getId();
            tilMedic.setError(null);
        });

        // Initialize date with today
        java.util.Calendar cal = java.util.Calendar.getInstance();
        selectedDate[0] = String.format(Locale.ROOT,"%04d-%02d-%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.DAY_OF_MONTH));
        tvSelectedDate.setText(String.format("Fecha: %02d/%02d/%04d", cal.get(java.util.Calendar.DAY_OF_MONTH), cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.YEAR)));

        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate[0] = String.format(Locale.ROOT, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(String.format(Locale.ROOT, "Fecha: %02d/%02d/%04d", dayOfMonth, month + 1, year));
        });

        chipGroupTimes.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null) {
                    selectedTime[0] = chip.getText().toString();
                    tvSelectedTime.setText("Horario: " + selectedTime[0]);
                }
            } else {
                selectedTime[0] = "";
                tvSelectedTime.setText("Seleccioná un horario");
            }
        });

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean valido = true;
                if (selectedIdPatient[0] == -1L) { tilPatient.setError("Seleccioná un paciente"); valido = false; }
                if (selectedIdMedic[0] == -1L) { tilMedic.setError("Seleccioná un médico"); valido = false; }
                if (selectedDate[0].isEmpty()) { Toast.makeText(requireContext(), "Seleccioná una fecha", Toast.LENGTH_SHORT).show(); valido = false; }
                if (selectedTime[0].isEmpty()) { Toast.makeText(requireContext(), "Seleccioná un horario", Toast.LENGTH_SHORT).show(); valido = false; }

                if (!valido) return;

                AppointmentDTO newAppt = new AppointmentDTO(selectedDate[0], selectedTime[0], selectedIdPatient[0], selectedIdMedic[0], StateAppointment.PENDING);
                if (appointmentDAO.insert(newAppt) > 0) {
                    Toast.makeText(requireContext(), "Turno guardado", Toast.LENGTH_SHORT).show();
                    loadAppointments();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
