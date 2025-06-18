package com.example.edushareandroid.ui.subirPublicacion;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.edushareandroid.R;
import com.example.edushareandroid.model.base_de_datos.Categoria;
import com.example.edushareandroid.model.base_de_datos.DocumentoRequest;
import com.example.edushareandroid.model.base_de_datos.Materia;
import com.example.edushareandroid.model.base_de_datos.PublicacionRequest;
import com.example.edushareandroid.model.base_de_datos.Rama;
import com.example.edushareandroid.network.grpc.FileServiceClient;
import com.example.edushareandroid.utils.ImageUtil;
import com.example.edushareandroid.utils.Resource;
import com.example.edushareandroid.utils.SesionUsuario;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PublicacionFragment extends Fragment {

    private PublicacionViewModel viewModel;
    private Spinner spnCategoria, spnRama, spnNivelEducativo, spnMateria;
    private List<Categoria> listaCategorias = new ArrayList<>();
    private List<Rama> listaRamas = new ArrayList<>();
    private List<Materia> listaMaterias = new ArrayList<>();
    private ArrayAdapter<String> categoriaAdapter;
    private ArrayAdapter<String> ramaAdapter;
    private ArrayAdapter<String> materiaAdapter;
    private static final int REQUEST_CODE_PICK_PDF = 1001;
    private Uri pdfUriSeleccionado;
    private FileServiceClient fileServiceClient;
    private String filePath; // Ruta del archivo PDF
    private String coverImagePath; // Ruta de la imagen de portada
    private ImageView imgPreview; // Para mostrar la portada
    private TextView txtFileName; // Para mostrar el nombre del archivo
    private LinearLayout uploadSuccessLayout;
    private EditText inputContenido;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subir_archivo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PublicacionViewModel.class);

        // Referencias de UI
        inputContenido = view.findViewById(R.id.input_contenido);
        spnCategoria = view.findViewById(R.id.spn_categoria);
        spnRama = view.findViewById(R.id.spn_rama);
        spnNivelEducativo = view.findViewById(R.id.spn_nivel_educativo);
        spnMateria = view.findViewById(R.id.spn_materia);
        imgPreview = view.findViewById(R.id.img_preview);
        txtFileName = view.findViewById(R.id.txt_file_name);
        uploadSuccessLayout = view.findViewById(R.id.upload_success_layout);
        Button btnSubir = view.findViewById(R.id.btn_subir);
        CardView cardUpload = view.findViewById(R.id.card_upload);

        // Inicializar lógica
        uploadSuccessLayout.setVisibility(View.GONE);
        configurarSpinners();
        observarViewModel();
        viewModel.cargarCategorias();
        viewModel.cargarRamas();
        fileServiceClient = new FileServiceClient();

        cardUpload.setOnClickListener(v -> abrirExploradorArchivos());

        btnSubir.setOnClickListener(v -> {
            // Validaciones previas
            if (filePath == null || coverImagePath == null) {
                Toast.makeText(requireContext(), "Primero sube un archivo PDF", Toast.LENGTH_SHORT).show();
                return;
            }

            int posCategoria = spnCategoria.getSelectedItemPosition();
            int posRama = spnRama.getSelectedItemPosition();
            int posMateria = spnMateria.getSelectedItemPosition();
            int posNivel = spnNivelEducativo.getSelectedItemPosition();
            String resumen = inputContenido.getText().toString().trim();

            if (posCategoria < 0 || posRama < 0 || posMateria < 0 || posNivel < 0 || resumen.isEmpty()) {
                Toast.makeText(requireContext(), "Completa todos los campos, incluyendo el resumen", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear documento
            String nombreArchivo = obtenerNombreArchivo(pdfUriSeleccionado);
            DocumentoRequest documentoRequest = new DocumentoRequest();
            documentoRequest.setTitulo(nombreArchivo);
            documentoRequest.setRuta(filePath);

            viewModel.crearDocumento(documentoRequest).observe(getViewLifecycleOwner(), docResult -> {
                if (docResult.getStatus() == Resource.Status.SUCCESS) {
                    int idDocumento = docResult.getData();
                    PublicacionRequest pub = new PublicacionRequest();
                    pub.setIdDocumento(idDocumento);
                    pub.setIdCategoria(listaCategorias.get(posCategoria).getIdCategoria());
                    pub.setIdMateriaYRama(listaMaterias.get(posMateria).getIdMateria());
                    pub.setNivelEducativo(spnNivelEducativo.getSelectedItem().toString());
                    pub.setResuContenido(resumen);

                    viewModel.crearPublicacion(pub).observe(getViewLifecycleOwner(), pubResult -> {
                        Toast.makeText(requireContext(), "Publicación creada exitosamente", Toast.LENGTH_LONG).show();
                        limpiarFormulario();
                        Navigation.findNavController(requireView()).popBackStack();
                    });
                }
            });
        });
    }


    private void limpiarFormulario() {
        txtFileName.setText("");
        imgPreview.setImageResource(R.drawable.ic_archivo);
        uploadSuccessLayout.setVisibility(View.GONE);
        filePath = null;
        coverImagePath = null;
        pdfUriSeleccionado = null;
        spnCategoria.setSelection(0);
        spnRama.setSelection(0);
        spnMateria.setSelection(0);
        spnNivelEducativo.setSelection(0);
    }


    private void abrirExploradorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo PDF"), REQUEST_CODE_PICK_PDF);
    }

    private void configurarSpinners() {
        // Categorías
        categoriaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(categoriaAdapter);

        // Ramas
        ramaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        ramaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRama.setAdapter(ramaAdapter);

        // Materias
        materiaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        materiaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMateria.setAdapter(materiaAdapter);

        // Nivel educativo desde recursos
        ArrayAdapter<CharSequence> nivelEducativoAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.niveles_educativos,
                android.R.layout.simple_spinner_item
        );
        nivelEducativoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNivelEducativo.setAdapter(nivelEducativoAdapter);

        // Escucha para ramas -> cargar materias
        spnRama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaRamas.size()) {
                    int idRama = listaRamas.get(position).getIdRama();
                    viewModel.cargarMateriasPorRama(idRama);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void observarViewModel() {
        viewModel.getCategorias().observe(getViewLifecycleOwner(), categorias -> {
            if (categorias != null) {
                listaCategorias = categorias;
                List<String> nombres = new ArrayList<>();
                for (Categoria c : categorias) nombres.add(c.getNombreCategoria());
                categoriaAdapter.clear();
                categoriaAdapter.addAll(nombres);
                categoriaAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "No se pudieron cargar las categorías", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getRamas().observe(getViewLifecycleOwner(), ramas -> {
            if (ramas != null) {
                listaRamas = ramas;
                List<String> nombres = new ArrayList<>();
                for (Rama r : ramas) nombres.add(r.getNombreRama());
                ramaAdapter.clear();
                ramaAdapter.addAll(nombres);
                ramaAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "No se pudieron cargar las ramas", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getMaterias().observe(getViewLifecycleOwner(), materias -> {
            if (materias != null) {
                listaMaterias = materias;
                List<String> nombresMaterias = new ArrayList<>();
                for (Materia m : materias) nombresMaterias.add(m.getNombreMateria());
                materiaAdapter.clear();
                materiaAdapter.addAll(nombresMaterias);
                materiaAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "No se pudieron cargar las materias", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_PDF && resultCode == RESULT_OK && data != null) {
            pdfUriSeleccionado = data.getData();

            if (pdfUriSeleccionado != null && requireContext().getContentResolver().getType(pdfUriSeleccionado).equals("application/pdf")) {
                subirPdfSeleccionado(pdfUriSeleccionado);
            } else {
                Toast.makeText(requireContext(), "Por favor selecciona un archivo PDF válido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subirPdfSeleccionado(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            byte[] pdfData = new byte[inputStream.available()];
            inputStream.read(pdfData);
            inputStream.close();
            String filename = obtenerNombreArchivo(uri);
            String username = SesionUsuario.obtenerNombreUsuario(requireContext());
            fileServiceClient.uploadPdf(pdfData, username, filename, new FileServiceClient.UploadCallback() {
                @Override
                public void onSuccess(String filePath, String coverImagePath) {
                    requireActivity().runOnUiThread(() -> {
                        PublicacionFragment.this.filePath = filePath;
                        PublicacionFragment.this.coverImagePath = coverImagePath;
                        uploadSuccessLayout.setVisibility(View.VISIBLE);
                        txtFileName.setText(filename);
                        descargarYMostrarPortada(coverImagePath);
                        Toast.makeText(requireContext(), "Archivo subido correctamente", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error al subir archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            });

        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error al leer archivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void descargarYMostrarPortada(String coverImagePath) {
        fileServiceClient.downloadCover(coverImagePath, new FileServiceClient.DownloadCallback() {
            @Override
            public void onSuccess(byte[] fileData, String filename) {
                requireActivity().runOnUiThread(() -> {
                    Bitmap coverBitmap = ImageUtil.binaryToBitmap(fileData);
                    imgPreview.setImageBitmap(coverBitmap);
                    imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    imgPreview.setImageResource(R.drawable.ic_archivo);
                    Toast.makeText(requireContext(), "No se pudo cargar la portada", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @SuppressLint("Range")
    private String obtenerNombreArchivo(Uri uri) {
        String resultado = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                resultado = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }
        }
        if (resultado == null) {
            resultado = uri.getLastPathSegment();
        }
        return resultado;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fileServiceClient != null) {
            fileServiceClient.shutdown();
        }
    }
}
