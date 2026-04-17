from rest_framework import serializers
from .models import Patient, Medic, Appointment

class PatientSerializer(serializers.ModelSerializer):
    class Meta:
        model = Patient
        fields = '__all__' 

class MedicSerializer(serializers.ModelSerializer):
    class Meta:
        model = Medic
        fields = '__all__'

class AppointmentSerializer(serializers.ModelSerializer):
    patientName = serializers.SerializerMethodField()
    medicName = serializers.SerializerMethodField()

    class Meta:
        model = Appointment
        fields = ['id', 'idPatient', 'idMedic', 'date', 'time', 'state', 'active', 'patientName', 'medicName']

    def get_patientName(self, obj):
        try:
            p = Patient.objects.get(id=obj.idPatient)
            return f"{p.name} {p.lastName}"
        except Patient.DoesNotExist:
            return "Paciente no encontrado"

    def get_medicName(self, obj):
        try:
            m = Medic.objects.get(id=obj.idMedic)
            return f"{m.name} {m.lastName}"
        except Medic.DoesNotExist:
            return "Médico no encontrado"