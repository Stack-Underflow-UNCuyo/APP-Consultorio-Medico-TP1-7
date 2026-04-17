from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.permissions import AllowAny
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from .models import Patient, Medic, Appointment
from .serializers import PatientSerializer, MedicSerializer, AppointmentSerializer, UserSerializer

class AuthLoginView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get('email')
        password = request.data.get('password')
        
        if not email or not password:
            return Response({"error": "Please provide email and password"}, status=status.HTTP_400_BAD_REQUEST)
        
        user = None
        try:
            user_obj = User.objects.get(email=email)
            user = authenticate(username=user_obj.username, password=password)
        except User.DoesNotExist:
            user = authenticate(username=email, password=password) # fallback

        if not user:
            return Response({"error": "Invalid credentials"}, status=status.HTTP_401_UNAUTHORIZED)
        
        # Lógica mejorada para obtener el ID real
        if Patient.objects.filter(email=email).exists():
            role = "PATIENT"
            patient = Patient.objects.filter(email=email).first()
            user_id = patient.id
        else:
            role = "MEDIC"
            medic = Medic.objects.filter(email=email).first()
            user_id = medic.id if medic else -1 

        name = user.get_full_name().strip() or user.username
        refresh = RefreshToken.for_user(user)

        return Response({
            "token": str(refresh.access_token),
            "role": role,
            "email": email,
            "name": name,
            "id": user_id  # <--- Fundamental para que Android no de sesión inválida
        }, status=status.HTTP_200_OK)


class AuthRegisterView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        data = request.data
        
        email = data.get('email')
        password = data.get('password')
        role = data.get('role')
        nombre = data.get('nombre', '')
        apellido = data.get('apellido', '')
        dni = data.get('dni')
        telefono = data.get('telefono', '')

        if not email or not password or not role:
            return Response({"error": "Faltan campos obligatorios (email, password, role)"}, status=status.HTTP_400_BAD_REQUEST)

        if User.objects.filter(username=email).exists():
            return Response({"error": "Este email ya está registrado"}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = User.objects.create_user(
                username=email, 
                email=email, 
                password=password, 
                first_name=nombre, 
                last_name=apellido
            )

            user_id = None

            if role == "PATIENT":
                paciente = Patient.objects.create(
                    name=nombre,
                    lastName=apellido,
                    email=email,
                    dni=dni,
                    phone=telefono
                )
                user_id = paciente.id
                
            elif role == "MEDIC":
                medico = Medic.objects.create(
                    name=nombre,
                    lastName=apellido,
                    email=email,
                    registration=f"PENDIENTE-{user.id}", 
                    speciality="Medicina General"
                )
                user_id = medico.id

            name = user.get_full_name().strip() or user.username
            refresh = RefreshToken.for_user(user)

            return Response({
                "token": str(refresh.access_token),
                "role": role,
                "email": email,
                "name": name,
                "id": user_id
            }, status=status.HTTP_200_OK)

        except Exception as e:
            if 'user' in locals():
                user.delete()
            return Response({"error": f"Error al registrar: {str(e)}"}, status=status.HTTP_400_BAD_REQUEST)

class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.filter(active=True) 
    serializer_class = PatientSerializer

class MedicViewSet(viewsets.ModelViewSet):
    queryset = Medic.objects.filter(active=True)
    serializer_class = MedicSerializer

class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.filter(active=True)
    serializer_class = AppointmentSerializer

    def by_patient(self, request, patientId=None):
        appointments = self.queryset.filter(idPatient=patientId)
        serializer = self.get_serializer(appointments, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

    def by_medic(self, request, medicId=None):
        appointments = self.queryset.filter(idMedic=medicId)
        serializer = self.get_serializer(appointments, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

class UserViewSet(viewsets.ModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserSerializer
    permission_classes = [AllowAny]