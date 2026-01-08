const express = require('express');
const { exec } = require('child_process');

const app = express();
app.use(express.json());

app.post('/scan', (req, res) => {
  const { scanId, projectKey, projectType, sourceDir } = req.body;

  console.log('=== Receive scan job ===');
  console.log(req.body);

  // ตอบกลับ n8n ทันที (ไม่ต้องรอ scan เสร็จ)
  res.json({
    status: 'QUEUED',
    scanId
  });

  // ทำงานต่อ background
  const cmd = `
    cd ${sourceDir} && \
    chmod +x mvnw && \
    ./mvnw -B -DskipTests compile && \
    sonar-scanner \
      -Dsonar.projectKey=${projectKey} \
      -Dsonar.sources=src/main/java \
      -Dsonar.java.binaries=target/classes \
      -Dsonar.host.url=${process.env.SONAR_HOST_URL} \
      -Dsonar.login=${process.env.SONAR_TOKEN}
  `;

  exec(cmd, (error, stdout, stderr) => {
    if (error) {
      console.error('Scan failed');
      console.error('STDOUT:', stdout);
      console.error('STDERR:', stderr);
      return;
    }

    console.log('Scan success');
    console.log(stdout);
  });
});

app.listen(8090, () => {
  console.log('Scan-worker listening on port 8090');
});
