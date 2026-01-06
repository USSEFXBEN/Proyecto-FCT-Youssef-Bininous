import { initializeApp } from "firebase/app";
import {
  getFirestore,
  collection,
  addDoc,
  setDoc,
  doc,
  Timestamp
} from "firebase/firestore";

const firebaseConfig = {
  // TU CONFIG DE FIREBASE
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function seed() {

  // ---------- USERS ----------
  const adminId = "ADMIN_UID_EJEMPLO";

  await setDoc(doc(db, "users", adminId), {
    nombre: "Youssef Bininous",
    email: "admin@example.com",
    rol: "admin",
    createdAt: Timestamp.now()
  });

  // ---------- ROUTINES ----------
  const rutinaRef = await addDoc(collection(db, "routines"), {
    userId: adminId,
    nombre: "Revisión del sistema",
    descripcion: "Ver estadísticas y usuarios",
    horaRecordatorio: "09:00",
    diasActivos: {
      lunes: true,
      miercoles: true,
      viernes: true
    }
  });

  // ---------- PROGRESS ----------
  await addDoc(collection(db, "progress"), {
    userId: adminId,
    rutinaId: rutinaRef.id,
    fecha: "2025-10-24",
    estado: "pendiente"
  });

  // ---------- RECORDATORIOS ----------
  await addDoc(collection(db, "recordatorios"), {
    userId: adminId,
    rutinaId: rutinaRef.id,
    hora: "09:00",
    mensaje: "¡Revisar usuarios y estadísticas!"
  });

  console.log("✅ Seed completado correctamente");
}

seed();
