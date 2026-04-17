from rest_framework import serializers
from .models import Patient, Medic, Appointment
from django.contrib.auth.models import User

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
        

class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True)
    role = serializers.CharField(write_only=True)
    name = serializers.CharField(write_only=True)
    lastName = serializers.CharField(write_only=True)
    dni = serializers.CharField(write_only=True, required=False)
    phone = serializers.CharField(write_only=True, required=False)

    def validate_dni(self, value):
        if Patient.objects.filter(dni=value).exists():
            raise serializers.ValidationError("Este DNI ya pertenece a un paciente registrado.")
        return value

    def validate_email(self, value):
        if User.objects.filter(email=value).exists():
            raise serializers.ValidationError("Este correo electrónico ya está en uso.")
        return value

    class Meta:
        model = User
        fields = ['email', 'password', 'role', 'name', 'lastName', 'dni', 'phone']

    def create(self, validated_data):
        role = validated_data.pop('role')
        
        # Crear el usuario base
        user = User.objects.create_user(
            username=validated_data['email'],
            email=validated_data['email'],
            password=validated_data['password'],
            first_name=validated_data['name'],
            last_name=validated_data['lastName']
        )

        # rol
        if role == "PATIENT":
            Patient.objects.create(
                name=validated_data['name'],
                lastName=validated_data['lastName'],
                email=validated_data['email'],
                dni=int(validated_data.get('dni', 0)),
                phone=validated_data.get('phone', '')
            )
        elif role == "MEDIC":
            Medic.objects.create(
                name=validated_data['name'],
                lastName=validated_data['lastName'],
                registration=f"REG-{validated_data.get('dni', '000')}", 
                speciality="General" 
            )
        return user