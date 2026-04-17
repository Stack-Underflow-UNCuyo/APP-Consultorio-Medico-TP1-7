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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import business.entities.AppointmentDTO;
import business.entities.MedicDTO;
import business.entities.PatientDTO;
import business.security.TokenManager;
import business.services.AppointmentService;

public class TurnosFragment extends Fragment {

    private RecyclerView rvAppointments;
    private LinearLayout layoutEmpty;
    private TextView tvCountPending;
    private TextView tvCountToday;
    private FloatingActionButton fab;

    private AppointmentService appointmentService;
    private AppointmentAdapter appointmentAdapter;
    private List<AppointmentDTO> appointmentList;
    private List<AppointmentDTO> todayAppointments;
    private List<AppointmentDTO> pendingAppointments;
    private ChipGroup chipGroupFilter;
    private TokenManager tokenManager;

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
        appointmentService = new AppointmentService(getContext());
        tokenManager = new TokenManager(requireContext());

        loadAppointments();

        fab.setOnClickListener(v -> showDialogNewAppointment());

        chipGroupFilter.setOnCheckedStateChangeListener((chipGroup, list) -> {
            if (!list.isEmpty()) {
                for (Integer id : list) {
                    if (id == R.id.chip_pendientes) {
                        updateViewList(pendingAppointments);
                    } else if (id == R.id.chip_todos) {
                        updateViewList(appointmentList);
                    } else if (id == R.id.chip_group_hoy) {
                        updateViewList(todayAppointments);
                    }
                }
            }
        });

        return view;
    }

    private void updateViewList(List<AppointmentDTO> list) {
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
        long userId = tokenManager.getId();
        String role = tokenManager.getRole();

        if (userId == -1L) {
            Toast.makeText(getContext(), "Sesión inválida. Por favor, vuelve a ingresar.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppointmentService.OnAppointmentsLoaded callback = new AppointmentService.OnAppointmentsLoaded() {
            @Override
            public void onSuccess(List<AppointmentDTO> appointments) {
                if (!isAdded() || getContext() == null) return;

                appointmentList = appointments;
                pendingAppointments = new ArrayList<>();
                todayAppointments = new ArrayList<>();

                int countToday = 0;
                int countFuture = 0;
                java.time.LocalDate today = java.time.LocalDate.now();

                List<AppointmentDTO> allPending = appointmentService.getPendingAppointments(appointmentList);
                for (AppointmentDTO appt : allPending) {
                    try {
                        java.time.LocalDate apptDate = java.time.LocalDate.parse(appt.getDate());
                        pendingAppointments.add(appt);

                        if (apptDate.isEqual(today)) {
                            countToday++;
                            todayAppointments.add(appt);
                        } else {
                            countFuture++;
                        }
                    } catch (Exception ignored) {}
                }

                tvCountToday.setText(String.valueOf(countToday));
                tvCountPending.setText(String.valueOf(countFuture));

                chipGroupFilter.check(R.id.chip_todos);
                updateViewList(appointmentList);
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        };

        if ("MEDIC".equals(role)) {
            appointmentService.getAppointmentsByMedic(userId, callback);
        } else {
            appointmentService.getAppointmentsByPatient(userId, callback);
        }
    }

    private void showDialogNewAppointment() {
        // ... (el código del diálogo se mantiene igual)
    }
}
