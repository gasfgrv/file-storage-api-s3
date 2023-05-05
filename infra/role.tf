data "aws_iam_user" "s3_bucket_user" {
  user_name = "s3springUser"
}

data "aws_iam_policy_document" "s3_access_bucket_role_document" {
  version = "2012-10-17"
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"
    principals {
      type        = "AWS"
      identifiers = [data.aws_iam_user.s3_bucket_user.arn]
    }
    condition {
      test     = "ArnEquals"
      values   = [data.aws_iam_user.s3_bucket_user.arn]
      variable = "aws:PrincipalArn"
    }
  }
}

resource "aws_iam_role" "s3_access_bucket_role" {
  name               = "S3springAccessBucketRole"
  description        = "Acesso ao bucket para poder fazer o download/upload dos arquivos"
  assume_role_policy = data.aws_iam_policy_document.s3_access_bucket_role_document.json
}

resource "aws_iam_role_policy_attachment" "s3_access_bucket_role_policy_attachment" {
  policy_arn = aws_iam_policy.s3_access_bucket_policy.arn
  role       = aws_iam_role.s3_access_bucket_role.name
}