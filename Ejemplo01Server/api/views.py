from rest_framework import viewsets
from .models import Patient, Medic, Appointment
from .serializers import PatientSerializer, MedicSerializer, AppointmentSerializer

class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.filter(active=True) 
    serializer_class = PatientSerializer

class MedicViewSet(viewsets.ModelViewSet):
    queryset = Medic.objects.filter(active=True)
    serializer_class = MedicSerializer

class AppointmentViewSet(viewsets.ModelViewSet):
    queryset = Appointment.objects.filter(active=True)
    serializer_class = AppointmentSerializer