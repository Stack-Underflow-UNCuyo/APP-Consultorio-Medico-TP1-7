from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import PatientViewSet, MedicViewSet, AppointmentViewSet, AuthLoginView, UserViewSet, AuthRegisterView

# El Router de DRF crea las URLs para GET, POST, PUT y DELETE
router = DefaultRouter()
router.register(r'patients', PatientViewSet)
router.register(r'medics', MedicViewSet)
router.register(r'appointments', AppointmentViewSet)
router.register(r'users', UserViewSet)

urlpatterns = [
    path('auth/login', AuthLoginView.as_view(), name='auth-login'),
    path('auth/register', AuthRegisterView.as_view(), name='auth-register'),
    path('appointments/patient/<int:patientId>', AppointmentViewSet.as_view({'get': 'by_patient'}), name='appointments-by-patient'),
    path('appointments/medic/<int:medicId>', AppointmentViewSet.as_view({'get': 'by_medic'}), name='appointments-by-medic'),
    path('', include(router.urls)),
]