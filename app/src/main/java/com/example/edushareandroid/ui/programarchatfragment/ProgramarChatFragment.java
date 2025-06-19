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

import com.example.edushareandroid.R;
import com.example.edushareandroid.databinding.FragmentProgramarchatBinding;
import com.example.edushareandroid.network.websocket.WebSocketManager;
import com.example.edushareandroid.utils.SesionUsuario;

import org.json.JSONObject;

import java.util.Calendar;

import android.util.Log;

public class ProgramarChatFragment extends Fragment {

    private FragmentProgramarchatBinding binding;
    private final Calendar calendar = Calendar.getInstance();
    private ProgramarChatViewModel viewModel;

    private String usuarioId;
    private String nombreUsuario;
    private boolean chatEnviado = false;

    private static final String TAG = "ProgramarChatFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView iniciado");

        try {
            binding = FragmentProgramarchatBinding.inflate(inflater, container, false);
            Log.d(TAG, "Binding inflado correctamente");

            if (getContext() == null || getActivity() == null) {
                Log.e(TAG, "Contexto o actividad es null");
                return null;
            }

            viewModel = new ViewModelProvider(requireActivity()).get(ProgramarChatViewModel.class);
            Log.d(TAG, "ViewModel obtenido correctamente");

            try {
                var datosUsuario = SesionUsuario.obtenerDatosUsuario(requireContext());
                usuarioId = String.valueOf(datosUsuario.getIdUsuario());
                nombreUsuario = datosUsuario.getNombreUsuario();
                Log.d(TAG, "Usuario ID: " + usuarioId + ", Nombre: " + nombreUsuario);
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener datos de usuario", e);
                usuarioId = "-1";
                nombreUsuario = "Usuario";
            }

            if (usuarioId.equals("-1")) {
                Log.e(TAG, "Usuario no está logueado");
                Toast.makeText(requireContext(), "Usuario no está logueado", Toast.LENGTH_SHORT).show();
                return null;
            }

            View view = binding.getRoot();
            Log.d(TAG, "Vista obtenida correctamente");

            if (binding.edtFecha == null || binding.edtHora == null ||
                    binding.btnGuardarChat == null || binding.edtTitulo == null ||
                    binding.edtDescripcion == null) {
                Log.e(TAG, "Uno o más elementos de la UI son null");
                return null;
            }

            Log.d(TAG, "Configurando UI...");
            configurarFechaPicker();
            configurarHoraPicker();
            configurarBotonGuardar();

            Log.d(TAG, "Conectando WebSocket...");
            conectarWebSocket();

            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error general en onCreateView", e);
            return null;
        }
    }

    private void configurarFechaPicker() {
        try {
            binding.edtFecha.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Fecha Picker clicked");
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                            (view, year1, month1, dayOfMonth) -> {
                                String fecha = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                                binding.edtFecha.setText(fecha);
                                Log.d(TAG, "Fecha seleccionada: " + fecha);
                            }, year, month, day);
                    datePicker.show();
                } catch (Exception e) {
                    Log.e(TAG, "Error en fecha picker", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar fecha picker", e);
        }
    }

    private void configurarHoraPicker() {
        try {
            binding.edtHora.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Hora Picker clicked");
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                            (view, hourOfDay, minute1) -> {
                                String hora = String.format("%02d:%02d", hourOfDay, minute1);
                                binding.edtHora.setText(hora);
                                Log.d(TAG, "Hora seleccionada: " + hora);
                            }, hour, minute, true);
                    timePicker.show();
                } catch (Exception e) {
                    Log.e(TAG, "Error en hora picker", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar hora picker", e);
        }
    }

    private void configurarBotonGuardar() {
        try {
            binding.btnGuardarChat.setOnClickListener(v -> {
                try {
                    if (chatEnviado) {
                        Log.d(TAG, "Chat ya enviado, ignorando clic");
                        return;
                    }

                    Log.d(TAG, "Botón Guardar Chat clicked");
                    String titulo = binding.edtTitulo.getText().toString().trim();
                    String descripcion = binding.edtDescripcion.getText().toString().trim();
                    String fecha = binding.edtFecha.getText().toString().trim();
                    String hora = binding.edtHora.getText().toString().trim();

                    if (fecha.isEmpty()) {
                        fecha = "";
                    }
                    if (hora.isEmpty()) {
                        hora = "";
                    }

                    if (titulo.isEmpty() || descripcion.isEmpty()) {
                        Log.e(TAG, "Campos vacíos");
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    chatEnviado = true;

                    binding.btnGuardarChat.setEnabled(false);
                    binding.btnGuardarChat.setText("Enviando...");

                    String nombreUsuario = SesionUsuario.obtenerDatosUsuario(requireContext()).getNombreUsuario();
                    Log.d(TAG, "Nombre de usuario: " + nombreUsuario);

                    JSONObject chatData = new JSONObject();
                    try {
                        chatData.put("Titulo", titulo);
                        chatData.put("Descripcion", descripcion);
                        chatData.put("Fecha", fecha);
                        chatData.put("Hora", hora);
                        chatData.put("IdAutor", usuarioId);
                        chatData.put("Autor", nombreUsuario);
                        chatData.put("NivelEducativo", "General");
                        chatData.put("Rama", "General");
                        chatData.put("Materia", "General");

                        Log.d(TAG, "Enviando datos del chat al WebSocket: " + chatData.toString());

                        WebSocketManager.getInstance().crearChat(chatData);
                        Toast.makeText(requireContext(), "Chat creado", Toast.LENGTH_SHORT).show();
                        regresarAlFragmentoAnterior();

                    } catch (Exception e) {
                        Log.e(TAG, "Error al crear JSON o enviar datos", e);
                        chatEnviado = false;
                        binding.btnGuardarChat.setEnabled(true);
                        binding.btnGuardarChat.setText("Guardar Chat");
                        Toast.makeText(requireContext(), "Error al programar el chat", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error en botón guardar", e);
                    chatEnviado = false;
                    if (binding != null) {
                        binding.btnGuardarChat.setEnabled(true);
                        binding.btnGuardarChat.setText("Guardar Chat");
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar botón guardar", e);
        }
    }


    private void conectarWebSocket() {
        try {
            if (WebSocketManager.getInstance().isConnected()) {
                Log.d(TAG, "La conexión WebSocket ya está activa.");
                return;
            }

            Log.d(TAG, "Conectando al WebSocket...");

            WebSocketManager.getInstance().connect(new WebSocketManager.WebSocketCallback() {
                @Override
                public void onMessageReceived(String action, JSONObject data) {
                    Log.d(TAG, "=== MENSAJE RECIBIDO ===");
                    Log.d(TAG, "Acción: " + action);
                    Log.d(TAG, "Data completa: " + data.toString());
                    Log.d(TAG, "========================");

                    if ("crear_chat".equals(action)) {
                        procesarRespuestaCrearChat(data);
                    } else if (data.has("accion") && "crear_chat".equals(data.optString("accion"))) {
                        procesarRespuestaCrearChat(data);
                    } else {
                        Log.d(TAG, "Mensaje recibido (no es crear_chat): " + action + " - " + data.toString());
                    }
                }

                @Override
                public void onConnectionOpened() {
                    Log.d(TAG, "Conexión WebSocket abierta");
                    if (getContext() != null) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Conexión establecida", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onConnectionClosed() {
                    Log.d(TAG, "Conexión WebSocket cerrada");
                    if (getContext() != null) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Conexión cerrada", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error WebSocket: " + error);
                    if (getContext() != null) {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Error de conexión: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }, usuarioId);
        } catch (Exception e) {
            Log.e(TAG, "Error al conectar WebSocket", e);
        }
    }

    private void procesarRespuestaCrearChat(JSONObject data) {
        try {
            String status = data.optString("status", "");
            Log.d(TAG, "Procesando respuesta crear_chat - Status: " + status);

            if ("ok".equals(status)) {
                String chatId = data.optString("IdChat", "");
                Log.d(TAG, "Chat creado exitosamente con ID: " + chatId);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Chat programado correctamente", Toast.LENGTH_SHORT).show();
                    regresarAlFragmentoAnterior();
                });

            } else if ("error".equals(status)) {
                String error = data.optString("error", "Error desconocido");
                Log.e(TAG, " Error del servidor: " + error);

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();

                    if (binding != null) {
                        binding.btnGuardarChat.setEnabled(true);
                        binding.btnGuardarChat.setText(R.string.guardar_chat);
                        chatEnviado = false;
                    }
                });
            } else {
                Log.w(TAG, "Status desconocido: " + status);
                Log.w(TAG, "Data completa: " + data.toString());

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Respuesta inesperada del servidor", Toast.LENGTH_SHORT).show();

                    if (binding != null) {
                        binding.btnGuardarChat.setEnabled(true);
                        binding.btnGuardarChat.setText(R.string.guardar_chat);
                        chatEnviado = false;
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al procesar respuesta del servidor", e);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Error al procesar respuesta", Toast.LENGTH_SHORT).show();

                if (binding != null) {
                    binding.btnGuardarChat.setEnabled(true);
                    binding.btnGuardarChat.setText("Guardar Chat");
                    chatEnviado = false;
                }
            });
        }
    }

    private void regresarAlFragmentoAnterior() {
        try {
            if (getActivity() != null) {
                requireActivity().onBackPressed();

                Log.d(TAG, "Regresando al fragmento anterior");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al regresar al fragmento anterior", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView - Limpiando recursos");

        chatEnviado = false;

        if (binding != null) {
            binding = null;
        }
    }
}