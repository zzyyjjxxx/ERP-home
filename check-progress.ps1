# ERP 项目进度检查 - PowerShell 版
# 用法: 右键 "使用 PowerShell 运行" 或在终端输入 .\check-progress.ps1

$projectDir = "C:\Users\Administrator\Desktop\项目\ERP-homeclaude"
Set-Location $projectDir

Write-Host "========== ERP 项目进度 ==========" -ForegroundColor Cyan
Write-Host "时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')"
Write-Host ""

# 总任务数
$total = (Select-String -Path "docs\superpowers\plans\2026-05-12-erp-implementation.md" -Pattern "^### Task" | Measure-Object).Count
Write-Host "总任务数: $total / 24"

# 最新提交
$lastCommit = git log --oneline -1 --format="%s"
$lastTime   = git log --oneline -1 --format="%ai"
Write-Host "最新提交: $lastCommit"
Write-Host "提交时间: $lastTime"
Write-Host ""

# 总提交数
$commitCount = (git log --oneline | Measure-Object).Count
Write-Host "总提交数: $commitCount"

# 未提交变更
$changes = (git status --short | Measure-Object).Count
if ($changes -gt 1) {
    Write-Host "未提交文件: $changes  个（正在工作中）" -ForegroundColor Yellow
} elseif ($changes -eq 1) {
    Write-Host "工作区基本干净（可能刚提交完）" -ForegroundColor Green
} else {
    Write-Host "工作区干净" -ForegroundColor Green
}

Write-Host ""

# 进程状态
$claudeRunning = Get-Process -Name "claude" -ErrorAction SilentlyContinue
if ($claudeRunning) {
    Write-Host "Claude 进程: 运行中" -ForegroundColor Green
} else {
    Write-Host "Claude 进程: 已停止" -ForegroundColor Red
}

Write-Host ""
Write-Host "--- 快捷操作 ---" -ForegroundColor Cyan
Write-Host "git log --oneline         # 看详细提交记录"
Write-Host "git status                # 看正在改哪些文件"
Write-Host ".\check-progress.ps1      # 刷新进度"

# 暂停
Read-Host "`n按 Enter 退出"
