from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.permissions import AllowAny
from rest_framework_simplejwt.tokens import RefreshToken
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from .models import Patient, Medic, Appointment
from .serializers import PatientSerializer, MedicSerializer, AppointmentSerializer

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
        
        if Patient.objects.filter(email=email).exists():
            role = "PATIENT"
        else:
            role = "MEDIC"

        refresh = RefreshToken.for_user(user)

        return Response({
            "token": str(refresh.access_token),
            "role": role,
            "email": email
        }, status=status.HTTP_200_OK)

class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.filter(active=True) 
    serializer_class = PatientSerializer

class MedicViewSet(viewsets.ModelViewSet):
    queryset = Medic.objects.filter(active=True)
    serializer_class = MedicSerializer

class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.filter(active=True)
    serializer_class = AppointmentSerializer