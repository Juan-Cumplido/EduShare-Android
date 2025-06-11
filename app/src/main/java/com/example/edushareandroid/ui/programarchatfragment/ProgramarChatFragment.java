package com.example.edushareandroid.ui.programarchatfragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.edushareandroid.databinding.FragmentProgramarchatBinding;

import java.util.Calendar;

public class ProgramarChatFragment extends Fragment {

    private FragmentProgramarchatBinding binding;
    private final Calendar calendar = Calendar.getInstance();
    private ProgramarChatViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProgramarchatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ProgramarChatViewModel.class);

        View view = binding.getRoot();

        configurarFechaPicker();
        configurarHoraPicker();
        configurarBotonGuardar();

        return view;
    }

    private void configurarFechaPicker() {
        binding.edtFecha.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String fecha = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        binding.edtFecha.setText(fecha);
                    }, year, month, day);
            datePicker.show();
        });
    }

    private void configurarHoraPicker() {
        binding.edtHora.setOnClickListener(v -> {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                    (view, hourOfDay, minute1) -> {
                        String hora = String.format("%02d:%02d", hourOfDay, minute1);
                        binding.edtHora.setText(hora);
                    }, hour, minute, true);
            timePicker.show();
        });
    }

    private void configurarBotonGuardar() {
        binding.btnGuardarChat.setOnClickListener(v -> {
            String titulo = binding.edtTitulo.getText().toString().trim();
            String descripcion = binding.edtDescripcion.getText().toString().trim();
            String fecha = binding.edtFecha.getText().toString().trim();
            String hora = binding.edtHora.getText().toString().trim();

            if (titulo.isEmpty() || descripcion.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí podrías guardar el chat programado en una base de datos, ViewModel, etc.
            Toast.makeText(requireContext(), "Chat programado correctamente", Toast.LENGTH_SHORT).show();

            // Opcional: volver atrás
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
