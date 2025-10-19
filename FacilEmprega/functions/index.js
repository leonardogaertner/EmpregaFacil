// functions/index.js (Sintaxe V2)

// Importa os módulos necessários
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.notifyNewVaga = onDocumentCreated("vagas/{vagaId}", async (event) => {

    // O evento V2 tem a propriedade data que contém o snapshot
    const snapshot = event.data.after;

    // Verifica se o documento existe
    if (!snapshot) {
        return;
    }

    const novaVaga = snapshot.data();
    const vagaId = event.params.vagaId; // Obtém o vagaId dos parâmetros

    const payload = {
        notification: {
            title: `Nova Vaga: ${novaVaga.cargo || "Oportunidade"}`,
            body: `${novaVaga.nomeEmpresa || "Uma Empresa"} publicou uma nova vaga! Confira.`,
        },
        data: {
            vagaId: vagaId,
            action: "open_vaga_list",
        },
    };

    const topic = "new_vaga_alerts";

    try {
        await admin.messaging().sendToTopic(topic, payload);
        console.log("Notificação V2 enviada com sucesso.");
    } catch (error) {
        console.error("Erro ao enviar notificação V2:", error);
    }
});