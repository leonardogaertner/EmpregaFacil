/**
 * Importa os módulos necessários do Firebase Functions e Admin SDK.
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Inicializa o Admin SDK para que o código possa interagir com o Firebase.
admin.initializeApp();

/**
 * Cloud Function acionada quando um novo documento é criado na coleção "vagas".
 * Envia uma notificação a todos os dispositivos inscritos no tópico "new_vaga_alerts".
 */
exports.notifyNewVaga = functions.firestore
    .document("vagas/{vagaId}")
    .onCreate(async (snapshot, context) => {

        // Obtém os dados da nova vaga criada
        const novaVaga = snapshot.data();

        // Constrói o payload da mensagem FCM
        const payload = {
            notification: {
                // Conteúdo exibido na notificação do Android
                title: `Nova Vaga: ${novaVaga.cargo || "Oportunidade"}`,
                body: `${novaVaga.nomeEmpresa || "Uma Empresa"} publicou uma nova vaga! Confira.`,
            },
            data: {
                // Dados que podem ser lidos pelo seu app (payload de dados)
                vagaId: context.params.vagaId,
                action: "open_vaga_list",
            },
        };

        const topic = "new_vaga_alerts";

        try {
            // Envia a mensagem para o tópico
            const response = await admin.messaging().sendToTopic(topic, payload);
            console.log("Notificação enviada com sucesso:", response);
            return response;
        } catch (error) {
            console.error("Erro ao enviar notificação:", error);
            return null; // Retorna null em caso de erro
        }
    });